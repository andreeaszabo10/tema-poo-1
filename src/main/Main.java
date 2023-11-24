package main;

import checker.Checker;
import checker.CheckerConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import fileio.input.*;
import basic.*;
import time.*;
import status.*;
import playlist.*;

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
        PlayerStatus playerStatus = new PlayerStatus(), back = new PlayerStatus();
        playerStatus.setLastTime(0);
        List<Playlist> playlists = new ArrayList<>();
        int alreadyLoaded = -1, repeat = 0;
        List<PodcastInput> loadedPodcasts = new ArrayList<>();
        boolean noSelect = false, loaded = false;
        String[] songsNoShuffle = null, songsShuffled = null;
        Map<String, List<String>> likedSongs = new HashMap<>();

        for (Command command : commands) {
            if (command instanceof SearchCommand searchCommand) {
                loaded = false;
                searchResults = SearchCommand.search(library, searchCommand, playlists);
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
                    selectedTrack = SelectCommand.select(searchResults, selectCommand);
                    if (!selectedTrack.equals("1")) {
                        noSelect = true;
                    }
                }
                outputs.add(SelectCommand.createSelectOutput(selectCommand,
                        selectedTrack, searchResults));
            }
            if (command instanceof LoadCommand loadCommand) {
                String message = LoadCommand.load(selectedTrack);
                if (noSelect) {
                    playerStatus.setLastTime(loadCommand.getTimestamp());
                }
                repeat = 0;
                loaded = true;
                if (selectedTrack != null && noSelect) {
                    searchResults = null;
                    loadCommand.setAlreadyLoaded(alreadyLoaded);
                    LoadCommand.loader(playerStatus, library, back, selectedTrack,
                            playlists, loadedPodcasts, loadCommand);
                    alreadyLoaded = loadCommand.getAlreadyLoaded();
                }
                outputs.add(LoadCommand.createLoadOutput(loadCommand, message, noSelect));
                noSelect = false;
            }
            if (command instanceof StatusCommand statusCommand) {
                statusCommand.setRepeat(repeat);
                StatusCommand.status(playerStatus, statusCommand,
                        library, selectedTrack, back, playlists);
                repeat = statusCommand.getRepeat();
                outputs.add(StatusCommand.createStatus(statusCommand, playerStatus));
            }
            if (command instanceof PlayPauseCommand playPauseCommand) {
                PlayPauseCommand.playPause(playPauseCommand, playerStatus);
                outputs.add(PlayPauseCommand.createPlayPauseOutput(playPauseCommand));
            }
            if (command instanceof CreatePlaylistCommand createPlaylist) {
                int var = Playlist.createPlaylist(createPlaylist, playlists);
                outputs.add(CreatePlaylistCommand.createPlaylistOutput(createPlaylist, var));
            }
            if (command instanceof AddRemoveCommand addRemove) {
                int x = -2;
                if (loaded) {
                    x = AddRemoveCommand.addRemove(addRemove, playlists, selectedTrack, library);
                }
                outputs.add(AddRemoveCommand.createAddRemoveOutput(addRemove, x));
            }
            if (command instanceof LikeCommand likeCommand) {
                int flag = 0;
                if (loaded) {
                    flag = LikeCommand.like(playerStatus, likeCommand, selectedTrack, likedSongs);
                }
                outputs.add(LikeCommand.createLikeOutput(likeCommand, flag, loaded));
            }
            if (command instanceof ShowPlaylistsCommand showPlay) {
                noSelect = false;
                outputs.add(ShowPlaylistsCommand.createShowPlaylistsOutput(showPlay, playlists));
            }
            if (command instanceof ShowSongsCommand show) {
                List<String> liked = LikeCommand.getLikedSongs(show.getUsername(), likedSongs);
                outputs.add(ShowSongsCommand.createOutput(show, liked));
            }
            if (command instanceof RepeatCommand repeatCommand) {
                repeatCommand.setRepeat(repeat);
                RepeatCommand.repeat(outputs, playerStatus, library, repeatCommand, loaded);
                repeat = repeatCommand.getRepeat();
            }
            if (command instanceof ShuffleCommand shuffle) {
                shuffle.setLoaded(loaded);
                shuffle.setSongsNoShuffle(songsNoShuffle);
                shuffle.setSongsShuffled(songsShuffled);
                ShuffleCommand.shuffle(shuffle, playerStatus, selectedTrack, playlists);
                songsShuffled = shuffle.getSongsShuffled();
                songsNoShuffle = shuffle.getSongsNoShuffle();
                loaded = shuffle.isLoaded();
                outputs.add(ShuffleCommand.createShuffleOutput(shuffle, playerStatus, loaded));
            }
            if (command instanceof NextCommand nextCommand) {
                NextCommand.next(playerStatus, library, playlists, selectedTrack, nextCommand);
                outputs.add(NextCommand.createNextOutput(nextCommand, playerStatus, loaded));
            }
            if (command instanceof PrevCommand prevCommand) {
                PrevCommand.prev(playerStatus, library, playlists, selectedTrack, prevCommand);
                outputs.add(PrevCommand.createPrevOutput(prevCommand, playerStatus, loaded));
            }
            if (command instanceof ForwardCommand forward) {
                ForwardCommand.forward(playerStatus, loaded);
                outputs.add(ForwardCommand.createForwardOutput(forward, playerStatus, loaded));
            }
            if (command instanceof BackwardCommand backward) {
                BackwardCommand.backward(playerStatus);
                outputs.add(BackwardCommand.createBackwardOutput(backward, playerStatus, loaded));
            }
            if (command instanceof SwitchVisibility swap) {
                Playlist playlist = SwitchVisibility.switchVisibility(playlists, swap);
                outputs.add(SwitchVisibility.createSwitchOutput(swap, playlist));
            }
            if (command instanceof FollowCommand follow) {
                int flag = FollowCommand.follow(noSelect, playlists, selectedTrack, follow);
                Playlist playlist = follow.getPlaylist();
                outputs.add(FollowCommand.createFollowOutput(follow, noSelect, playlist, flag));
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
     * finds the playlist with the wanted id from a user
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
     * function that searches for a song by name
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
     * function that searches for a podcast by name
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
     * function that searches for a playlist by name
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
