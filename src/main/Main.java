package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * The entry point to this homework.
 */
public final class Main {
    static final String LIBRARY_PATH = CheckerConstants.TESTS_PATH + "library/library.json";

    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * DO NOT MODIFY MAIN METHOD
     * Call the checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.getName().startsWith("library")) {
                continue;
            }

            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    private static final int SECOND = 90;

    /**
     * @param filePathInput for input file
     * @param filePathOutput for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePathInput,
                              final String filePathOutput) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        LibraryInput library = objectMapper.readValue(new File(LIBRARY_PATH), LibraryInput.class);

        ArrayNode outputs = objectMapper.createArrayNode();

        String filepath = CheckerConstants.TESTS_PATH + filePathInput;

        List<Command> commands = readCommandsFromFile(filepath);
        List<String> searchResults = new ArrayList<>();
        String selectedTrack = null;
        PlayerStatus playerStatus = new PlayerStatus();
        playerStatus.setLastTime(0);
        List<Playlist> playlists = new ArrayList<>();
        boolean loaded = false;
        PlayerStatus back = new PlayerStatus();
        int alreadyLoaded;
        List<PodcastInput> loadedPodcasts = new ArrayList<>();
        boolean noSelect = false;
        int repeat = 0;
        String[] songsNoShuffle = null;
        String[] songsShuffled = null;
        Map<String, List<String>> likedSongs = new HashMap<>();

        for (Command command : commands) {
            if (command instanceof SearchCommand searchCommand) {
                loaded = false;
                searchResults = SearchCommand.performSearch(library, searchCommand, playlists);
                if (!playerStatus.isPaused() && playerStatus.getType() != null) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            - searchCommand.getTimestamp() + playerStatus.getLastTime());
                    if (playerStatus.getType().equals("podcast")) {
                        back.setRemainedTime(playerStatus.getRemainedTime());
                    }
                }
                outputs.add(SearchCommand.createSearchOutput(searchCommand, searchResults));
            }
            if (command instanceof SelectCommand selectCommand) {
                if (searchResults != null) {
                    selectedTrack = SelectCommand.performSelect(searchResults, selectCommand);
                    if (!selectedTrack.equals("1")) {
                        noSelect = true;
                    }
                }
                outputs.add(SelectCommand.createSelectOutput(selectCommand,
                        selectedTrack, searchResults));
            }
            if (command instanceof LoadCommand loadCommand) {
                String message = LoadCommand.performLoad(selectedTrack);
                if (noSelect) {
                    playerStatus.setLastTime(loadCommand.getTimestamp());
                }
                SongInput song;
                PodcastInput podcast;
                Playlist playlist;
                repeat = 0;
                loaded = true;
                if (selectedTrack != null && noSelect) {
                    searchResults = null;
                    song = getSongDetails(library, selectedTrack);
                    podcast = getPodcastDetails(library, selectedTrack);
                    playlist = getPlaylistDetails(playlists, selectedTrack);
                    if (song != null) {
                        playerStatus.setRemainedTime(song.getDuration());
                        playerStatus.setCurrentTrack(song.getName());
                        playerStatus.setPaused(false);
                        playerStatus.setRepeatMode(0);
                        playerStatus.setShuffleMode(false);
                        playerStatus.setType("song");
                        playerStatus.setIndex(0);
                    }
                    if (podcast != null) {
                        ArrayList<EpisodeInput> list = podcast.getEpisodes();
                        EpisodeInput first = list.get(0);
                        alreadyLoaded = 0;
                        for (PodcastInput pod : loadedPodcasts) {
                            if (pod.getName().equals(podcast.getName())) {
                                alreadyLoaded = 1;
                                break;
                            }
                        }
                        if (alreadyLoaded == 1) {
                            playerStatus.setRemainedTime(back.getRemainedTime());
                            playerStatus.setCurrentTrack(first.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode(0);
                            playerStatus.setShuffleMode(false);
                            playerStatus.setType("podcast");
                            playerStatus.setIndex(0);
                            back = playerStatus;
                        } else {
                            playerStatus.setRemainedTime(first.getDuration());
                            playerStatus.setCurrentTrack(first.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode(0);
                            playerStatus.setShuffleMode(false);
                            playerStatus.setType("podcast");
                            playerStatus.setIndex(0);
                            back.setRemainedTime(first.getDuration());
                            loadedPodcasts.add(podcast);
                        }
                    }
                    if (playlist != null) {
                        playerStatus.setType("playlist");
                        String[] songs = playlist.getSongs();
                        if (songs.length != 0) {
                            song = getSongDetails(library, songs[0]);
                            assert song != null;
                            playerStatus.setRemainedTime(song.getDuration());
                            playerStatus.setCurrentTrack(song.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode(0);
                            playerStatus.setShuffleMode(false);
                            playerStatus.setIndex(0);
                        }
                    }
                }
                outputs.add(LoadCommand.createLoadOutput(loadCommand, message, noSelect));
                noSelect = false;
            }
            if (command instanceof StatusCommand statusCommand) {
                statusCommand.setRepeat(repeat);
                StatusCommand.status(playerStatus, statusCommand, library, selectedTrack, back, playlists);
                repeat = statusCommand.getRepeat();
                outputs.add(StatusCommand.createStatus(statusCommand, playerStatus));
            }
            if (command instanceof PlayPauseCommand playPauseCommand) {
                if (playerStatus.isPaused()) {
                    playPauseCommand.setPaused(0);
                    playerStatus.setLastTime(playPauseCommand.getTimestamp());
                    playerStatus.setPaused(false);
                } else {
                    playPauseCommand.setPaused(1);
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            - playPauseCommand.getTimestamp() + playerStatus.getLastTime());
                    playerStatus.setLastTime(playPauseCommand.getTimestamp());
                    playerStatus.setPaused(true);
                }
                PlayPauseCommand.performPlayPause(playPauseCommand);
                outputs.add(PlayPauseCommand.createPlayPauseOutput(playPauseCommand));
            }
            if (command instanceof CreatePlaylistCommand createPlaylistCommand) {
                Playlist playlist = Playlist.performCreatePlaylist(createPlaylistCommand);
                int var = 0;
                for (Playlist play : playlists) {
                    if (play.getPlaylistName().equals(playlist.getPlaylistName())) {
                        var = 1;
                        break;
                    }
                }
                if (var == 0) {
                    playlists.add(playlist);
                }
                outputs.add(CreatePlaylistCommand.createPlaylistOutput(createPlaylistCommand,
                        var));
            }
            if (command instanceof AddRemoveCommand addRemoveCommand) {
                int x = -2;
                if (loaded) {
                    x = AddRemoveCommand.performAddRemove(addRemoveCommand,
                            playlists, selectedTrack, library);
                }
                outputs.add(AddRemoveCommand.createAddRemoveOutput(addRemoveCommand, x));
            }
            if (command instanceof LikeCommand likeCommand) {
                int flag = 0;
                assert playerStatus.getType() != null;
                if (loaded) {
                    flag = LikeCommand.like(playerStatus, likeCommand, selectedTrack, likedSongs);
                }
                outputs.add(LikeCommand.createLikeOutput(likeCommand, flag, loaded));
            }
            if (command instanceof ShowPlaylistsCommand showPlaylistsCommand) {
                noSelect = false;
                outputs.add(ShowPlaylistsCommand.createShowPlaylistsOutput(showPlaylistsCommand,
                        playlists));
            }
            if (command instanceof ShowSongsCommand showSongsCommand) {
                List<String> liked = LikeCommand
                        .getLikedSongs(showSongsCommand.getUsername(), likedSongs);
                outputs.add(ShowSongsCommand.createOutput(showSongsCommand, liked));
            }
            if (command instanceof RepeatCommand repeatCommand) {
                if (repeat == 2 && playerStatus.getType().equals("song")) {
                    SongInput song = getSongDetails(library, playerStatus.getCurrentTrack());
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            - repeatCommand.getTimestamp() + playerStatus.getLastTime());
                    playerStatus.setLastTime(repeatCommand.getTimestamp());
                    while (playerStatus.getRemainedTime() <= 0) {
                        assert song != null;
                        playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                                + song.getDuration());
                    }
                }
                if (loaded) {
                    repeat++;
                    if (repeat > 2) {
                        repeat = 0;
                    }
                    playerStatus.setRepeatMode(repeat);
                    if (playerStatus.getType() != null) {
                        outputs.add(RepeatCommand.createRepeatOutput(repeatCommand,
                                repeat, playerStatus));
                    }
                } else {
                    repeat = 3;
                    outputs.add(RepeatCommand.createRepeatOutput(repeatCommand,
                            repeat, playerStatus));
                    playerStatus.setRepeatMode(0);
                }
            }
            if (command instanceof ShuffleCommand shuffleCommand) {
                shuffleCommand.setLoaded(loaded);
                shuffleCommand.setSongsNoShuffle(songsNoShuffle);
                shuffleCommand.setSongsShuffled(songsShuffled);
                ShuffleCommand.shuffle(shuffleCommand, playerStatus, selectedTrack, playlists);
                songsShuffled = shuffleCommand.getSongsShuffled();
                songsNoShuffle = shuffleCommand.getSongsNoShuffle();
                loaded = shuffleCommand.isLoaded();
                outputs.add(ShuffleCommand.createShuffleOutput(shuffleCommand,
                        playerStatus, loaded));
            }
            if (command instanceof NextCommand nextCommand) {
                NextCommand.next(playerStatus, library, playlists, selectedTrack, nextCommand);
                outputs.add(NextCommand.createNextOutput(nextCommand, playerStatus, loaded));
            }
            if (command instanceof PrevCommand prevCommand) {
                PrevCommand.prev(playerStatus, library, playlists, selectedTrack, prevCommand);
                outputs.add(PrevCommand.createPrevOutput(prevCommand, playerStatus, loaded));
            }
            if (command instanceof ForwardCommand forwardCommand) {
                assert playerStatus.getType() != null;
                if (playerStatus.getType().equals("podcast")) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime() - SECOND);
                }
                if (!loaded) {
                    playerStatus.setRemainedTime(0);
                    playerStatus.setCurrentTrack("");
                    playerStatus.setPaused(true);
                    playerStatus.setRepeatMode(0);
                    playerStatus.setShuffleMode(false);
                }
                outputs.add(ForwardCommand.createForwardOutput(forwardCommand,
                        playerStatus, loaded));
            }
            if (command instanceof BackwardCommand backwardCommand) {
                assert playerStatus.getType() != null;
                if (playerStatus.getType().equals("podcast")) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime() + SECOND);
                }
                outputs.add(BackwardCommand.createBackwardOutput(backwardCommand,
                        playerStatus, loaded));
            }
            if (command instanceof SwitchVisibility switchVisibility) {
                Playlist playlist;
                int count = 0;
                for (Playlist p : playlists) {
                    if (p.getOwner().equals(switchVisibility.getUsername())) {
                        count++;
                    }
                }
                if (switchVisibility.getPlaylistId() <= count) {
                    playlist = findPlaylist(playlists,
                            switchVisibility.getUsername(), switchVisibility.getPlaylistId());
                    if (playlist.getVisibility().equals("public")) {
                        playlist.setVisibility("private");
                    } else {
                        playlist.setVisibility("public");
                    }
                } else {
                    playlist = null;
                }
                assert playlist != null;
                outputs.add(SwitchVisibility.createSwitchOutput(switchVisibility, playlist));
            }
            if (command instanceof FollowCommand followCommand) {
                int flag = FollowCommand.follow(noSelect, playlists, selectedTrack, followCommand);
                Playlist playlist = followCommand.getPlaylist();
                outputs.add(FollowCommand.createFollowOutput(followCommand,
                        noSelect, playlist, flag));
            }
            if (command instanceof GetTop5Songs getTop5Songs) {
                SongInput[] songs = GetTop5Songs.top5Songs(library, likedSongs);
                outputs.add(GetTop5Songs.createTop5Output(getTop5Songs, songs));
            }
            if (command instanceof GetTop5Playlists getTop5Playlists) {
                String[] array = GetTop5Playlists.top5Playlists(playlists);
                outputs.add(GetTop5Playlists.createTop5POutput(getTop5Playlists, array));
            }
        }
        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);

    }

    /**
     *
     */
    public static Playlist findPlaylist(final List<Playlist> playlists,
                                        final String username, final int playlistId) {
        Playlist playlist = new Playlist();
        int count = 0;
        for (Playlist p : playlists) {
            if (p.getOwner().equals(username)) {
                count++;
                if (count == playlistId) {
                    playlist = p;
                }
            }
        }
        return playlist;
    }

    /**
     *
     */
    public static SongInput getSongDetails(final LibraryInput library,
                                            final String name) {
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(name)) {
                return song;
            }
        }
        return null;
    }

    /**
     *
     */
    public static PodcastInput getPodcastDetails(final LibraryInput library,
                                                  final String name) {
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(name)) {
                return podcast;
            }
        }
        return null;
    }

    /**
     *
     */
    public static Playlist getPlaylistDetails(final List<Playlist> playlists,
                                               final String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getPlaylistName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }

    /**
     * @param filePathInput for input file
     * reading of the commands
     * @throws IOException in case of exceptions to reading / writing
     */
    private static List<Command> readCommandsFromFile(final String filePathInput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Command> commands = new ArrayList<>();

        Command[] commandArray = objectMapper.readValue(new File(filePathInput),
                Command[].class);

        for (Command command : commandArray) {
            if ("search".equals(command.getCommand())) {
                SearchCommand searchCommand = new SearchCommand();
                searchCommand.setTimestamp(command.getTimestamp());
                searchCommand.setUsername(command.getUsername());
                searchCommand.setType(command.getType());
                searchCommand.setFilters(command.getFilters());
                commands.add(searchCommand);
            } else if ("select".equals(command.getCommand())) {
                SelectCommand selectCommand = new SelectCommand();
                selectCommand.setTimestamp(command.getTimestamp());
                selectCommand.setUsername(command.getUsername());
                selectCommand.setItemNumber(command.getItemNumber());
                commands.add(selectCommand);
            } else if ("load".equals(command.getCommand())) {
                LoadCommand loadCommand = new LoadCommand();
                loadCommand.setTimestamp(command.getTimestamp());
                loadCommand.setUsername(command.getUsername());
                commands.add(loadCommand);
            } else if ("status".equals(command.getCommand())) {
                StatusCommand statusCommand = new StatusCommand();
                statusCommand.setTimestamp(command.getTimestamp());
                statusCommand.setUsername(command.getUsername());
                commands.add(statusCommand);
            } else if ("playPause".equals(command.getCommand())) {
                PlayPauseCommand playPauseCommand = new PlayPauseCommand();
                playPauseCommand.setTimestamp(command.getTimestamp());
                playPauseCommand.setUsername(command.getUsername());
                commands.add(playPauseCommand);
            } else if ("createPlaylist".equals(command.getCommand())) {
                CreatePlaylistCommand createPlaylistCommand = new CreatePlaylistCommand();
                createPlaylistCommand.setTimestamp(command.getTimestamp());
                createPlaylistCommand.setUsername(command.getUsername());
                createPlaylistCommand.setPlaylistName(command.getPlaylistName());
                commands.add(createPlaylistCommand);
            } else if ("addRemoveInPlaylist".equals(command.getCommand())) {
                AddRemoveCommand addRemoveCommand = new AddRemoveCommand();
                addRemoveCommand.setTimestamp(command.getTimestamp());
                addRemoveCommand.setUsername(command.getUsername());
                addRemoveCommand.setPlaylistId(command.getPlaylistId());
                commands.add(addRemoveCommand);
            } else if ("like".equals(command.getCommand())) {
                LikeCommand likeCommand = new LikeCommand();
                likeCommand.setTimestamp(command.getTimestamp());
                likeCommand.setUsername(command.getUsername());
                commands.add(likeCommand);
            } else if ("showPlaylists".equals(command.getCommand())) {
                ShowPlaylistsCommand showPlaylistsCommand = new ShowPlaylistsCommand();
                showPlaylistsCommand.setTimestamp(command.getTimestamp());
                showPlaylistsCommand.setUsername(command.getUsername());
                commands.add(showPlaylistsCommand);
            } else if ("showPreferredSongs".equals(command.getCommand())) {
                ShowSongsCommand showSongsCommand = new ShowSongsCommand();
                showSongsCommand.setTimestamp(command.getTimestamp());
                showSongsCommand.setUsername(command.getUsername());
                commands.add(showSongsCommand);
            } else if ("repeat".equals(command.getCommand())) {
                RepeatCommand repeatCommand = new RepeatCommand();
                repeatCommand.setTimestamp(command.getTimestamp());
                repeatCommand.setUsername(command.getUsername());
                commands.add(repeatCommand);
            } else if ("shuffle".equals(command.getCommand())) {
                ShuffleCommand shuffleCommand = new ShuffleCommand();
                shuffleCommand.setTimestamp(command.getTimestamp());
                shuffleCommand.setUsername(command.getUsername());
                shuffleCommand.setSeed(command.getSeed());
                commands.add(shuffleCommand);
            } else if ("next".equals(command.getCommand())) {
                NextCommand nextCommand = new NextCommand();
                nextCommand.setTimestamp(command.getTimestamp());
                nextCommand.setUsername(command.getUsername());
                commands.add(nextCommand);
            } else if ("prev".equals(command.getCommand())) {
                PrevCommand prevCommand = new PrevCommand();
                prevCommand.setTimestamp(command.getTimestamp());
                prevCommand.setUsername(command.getUsername());
                commands.add(prevCommand);
            } else if ("forward".equals(command.getCommand())) {
                ForwardCommand forwardCommand = new ForwardCommand();
                forwardCommand.setTimestamp(command.getTimestamp());
                forwardCommand.setUsername(command.getUsername());
                commands.add(forwardCommand);
            } else if ("backward".equals(command.getCommand())) {
                BackwardCommand backwardCommand = new BackwardCommand();
                backwardCommand.setTimestamp(command.getTimestamp());
                backwardCommand.setUsername(command.getUsername());
                commands.add(backwardCommand);
            } else if ("switchVisibility".equals(command.getCommand())) {
                SwitchVisibility switchVisibility = new SwitchVisibility();
                switchVisibility.setTimestamp(command.getTimestamp());
                switchVisibility.setUsername(command.getUsername());
                switchVisibility.setPlaylistId(command.getPlaylistId());
                commands.add(switchVisibility);
            } else if ("follow".equals(command.getCommand())) {
                FollowCommand followCommand = new FollowCommand();
                followCommand.setTimestamp(command.getTimestamp());
                followCommand.setUsername(command.getUsername());
                followCommand.setPlaylistId(command.getPlaylistId());
                commands.add(followCommand);
            }  else if ("getTop5Playlists".equals(command.getCommand())) {
                GetTop5Playlists getTop5Playlists = new GetTop5Playlists();
                getTop5Playlists.setTimestamp(command.getTimestamp());
                commands.add(getTop5Playlists);
            }  else if ("getTop5Songs".equals(command.getCommand())) {
                GetTop5Songs getTop5Songs = new GetTop5Songs();
                getTop5Songs.setTimestamp(command.getTimestamp());
                commands.add(getTop5Songs);
            }
        }
        return commands;
    }
}
