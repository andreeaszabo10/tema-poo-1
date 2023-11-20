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
        int lasttime = 0;
        List<Playlist> playlists = new ArrayList<>();
        boolean ok = false;
        List<String> liked = new ArrayList<>();
        boolean loaded = false;
        PlayerStatus back = new PlayerStatus();
        int alreadyLoaded;
        List<PodcastInput> loadedPodcasts = new ArrayList<>();
        boolean noSelect = false;


        for (Command command : commands) {
            if (command instanceof SearchCommand searchCommand) {
                loaded = false;
                searchResults = SearchCommand.performSearch(library, searchCommand, playlists);
                if (!playerStatus.isPaused() && playerStatus.getType() != null) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            - searchCommand.getTimestamp() + lasttime);
                    if (playerStatus.getType().equals("podcast")) {
                        back.setRemainedTime(playerStatus.getRemainedTime());
                    }
                }
                outputs.add(SearchCommand.createSearchOutput(searchCommand, searchResults));
            }
            if (command instanceof SelectCommand selectCommand) {
                selectedTrack = SelectCommand.performSelect(searchResults, selectCommand);
                outputs.add(SelectCommand.createSelectOutput(selectCommand, selectedTrack));
                noSelect = true;
            }
            if (command instanceof LoadCommand loadCommand) {
                String message = LoadCommand.performLoad(selectedTrack);
                if (noSelect) {
                    lasttime = loadCommand.getTimestamp();
                }
                SongInput song;
                PodcastInput podcast;
                Playlist playlist;
                ok = false;
                loaded = true;
                if (selectedTrack != null && noSelect == true) {
                    song = getSongDetails(library, selectedTrack);
                    podcast = getPodcastDetails(library, selectedTrack);
                    playlist = getPlaylistDetails(playlists, selectedTrack);
                    if (song != null) {
                        playerStatus.setRemainedTime(song.getDuration());
                        playerStatus.setCurrentTrack(song.getName());
                        playerStatus.setPaused(false);
                        playerStatus.setRepeatMode("No Repeat");
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
                            //System.out.println(back.getRemainedTime());
                            playerStatus.setRemainedTime(back.getRemainedTime());
                            playerStatus.setCurrentTrack(first.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode("No Repeat");
                            playerStatus.setShuffleMode(false);
                            playerStatus.setType("podcast");
                            playerStatus.setIndex(0);
                            back = playerStatus;
                        } else {
                            playerStatus.setRemainedTime(first.getDuration());
                            playerStatus.setCurrentTrack(first.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode("No Repeat");
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
                        //System.out.println(Arrays.toString(songs));
                        if (songs.length != 0) {
                            song = getSongDetails(library, songs[0]);
                            //System.out.println(song.getName());
                            playerStatus.setRemainedTime(song.getDuration());
                            playerStatus.setCurrentTrack(song.getName());
                            playerStatus.setPaused(false);
                            playerStatus.setRepeatMode("No Repeat");
                            playerStatus.setShuffleMode(false);
                            playerStatus.setIndex(0);
                        }
                    }
                }
                outputs.add(LoadCommand.createLoadOutput(loadCommand, message, noSelect));
                noSelect = false;
            }
            if (command instanceof StatusCommand statusCommand) {
                if (playerStatus.getType().equals("podcast")) {
                    if (playerStatus.getRemainedTime() - statusCommand.getTimestamp() + lasttime < 0) {
                        PodcastInput podcast = getPodcastDetails(library, selectedTrack);
                        if (podcast != null) {
                            ArrayList<EpisodeInput> list = podcast.getEpisodes();
                            playerStatus.setIndex(playerStatus.getIndex() + 1);
                            int index = playerStatus.getIndex();
                            EpisodeInput episode = list.get(index);
                            playerStatus.setCurrentTrack(episode.getName());
                            back.setRemainedTime(episode.getDuration() - (-playerStatus.getRemainedTime()));
                            playerStatus.setRemainedTime(back.getRemainedTime());
                        }
                    }
                }
                if (!playerStatus.isPaused()) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime()
                            - statusCommand.getTimestamp() + lasttime);
                    if (playerStatus.getType().equals("podcast")) {
                        back.setRemainedTime(playerStatus.getRemainedTime());
                    }
                }
                int right = 0;
                if (playerStatus.getType().equals("playlist"))  {
                    if (playerStatus.getRemainedTime() < 0) {
                        //System.out.println("1");
                        Playlist playlist = getPlaylistDetails(playlists, selectedTrack);
                        if (playlist != null) {
                            String[] songs = playlist.getSongs();
                            int var = 0;
                            for (String i : songs) {
                                SongInput song = getSongDetails(library, i);
                                if (var != 0 && song != null) {
                                   // System.out.println(song.getDuration());
                                    if (song.getDuration() + playerStatus.getRemainedTime() > 0) {
                                        right = song.getDuration();
                                        playerStatus.setCurrentTrack(song.getName());
                                        //System.out.println(playerStatus.getCurrentTrack());
                                        break;
                                    }
                                }
                                var = 1;
                            }
                            if (right != 0) {
                                playerStatus.setRemainedTime(right - (-playerStatus.getRemainedTime()));
                                //System.out.println(playerStatus.getRemainedTime());
                            }
                            //playerStatus.setRemainedTime(back.getRemainedTime());
                        }
                    }
                }
                lasttime = statusCommand.getTimestamp();
                if (playerStatus.getType().equals("song") || playerStatus.getType().equals("podcast")
                        || playerStatus.getRemainedTime() < 0) {
                    if (statusCommand.getTimestamp() > lasttime + playerStatus.getRemainedTime()) {
                        playerStatus.setRemainedTime(0);
                        playerStatus.setCurrentTrack("");
                        playerStatus.setPaused(true);
                        playerStatus.setRepeatMode("No Repeat");
                        playerStatus.setShuffleMode(false);
                    }
                }

                outputs.add(StatusCommand.createStatus(statusCommand, playerStatus));
            }
            if (command instanceof PlayPauseCommand playPauseCommand) {
                if (playerStatus.isPaused()) {
                    playPauseCommand.setPaused(0);
                    lasttime = playPauseCommand.getTimestamp();
                    playerStatus.setPaused(false);
                } else {
                    playPauseCommand.setPaused(1);
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime() - playPauseCommand.getTimestamp() + lasttime);
                    lasttime = playPauseCommand.getTimestamp();
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

                outputs.add(CreatePlaylistCommand.createPlaylistOutput(createPlaylistCommand, var));
            }
            if (command instanceof AddRemoveCommand addRemoveCommand) {
                int x = -2;
                if (loaded) {
                    x = performAddRemove(addRemoveCommand, playlists, selectedTrack, library);
                }
                outputs.add(AddRemoveCommand.createAddRemoveOutput(addRemoveCommand, x));
            }
            if (command instanceof LikeCommand likeCommand) {
                String a = LikeCommand.performLike(ok, selectedTrack);
                int var = 0;
                if (a != null) {
                    for (String x : liked) {
                        if (a.equals(x)) {
                            var = 1;
                            break;
                        }
                    }
                    if (var == 0) {
                        liked.add(a);
                    } else {
                        liked.remove(a);
                    }
                }
                outputs.add(LikeCommand.createLikeOutput(likeCommand, ok, loaded));
                ok = !ok;
            }
            if (command instanceof ShowPlaylistsCommand showPlaylistsCommand) {
                outputs.add(ShowPlaylistsCommand.createShowPlaylistsOutput(showPlaylistsCommand,
                        playlists));
            }
            if (command instanceof ShowSongsCommand showSongsCommand) {
                outputs.add(ShowSongsCommand.createOutput(showSongsCommand, liked));
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);

    }

    private static SongInput getSongDetails(final LibraryInput library, final String name) {
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(name)) {
                return song;
            }
        }
        return null;
    }
    private static PodcastInput getPodcastDetails(final LibraryInput library, final String name) {
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(name)) {
                return podcast;
            }
        }
        return null;
    }
    private static Playlist getPlaylistDetails(final List<Playlist> playlists, final String name) {
        for (Playlist playlist : playlists) {
            if (playlist.getPlaylistName().equals(name)) {
                return playlist;
            }
        }
        return null;
    }
    private static int performAddRemove(final AddRemoveCommand addRemoveCommand,
                                        final List<Playlist> playlists,
                                        final String song, final LibraryInput library) {
        if (addRemoveCommand.getPlaylistId() > playlists.size()) {
            return -1;
        }
        Playlist bun = playlists.get(addRemoveCommand.getPlaylistId() - 1);
        int ok = 1;
        if (bun.getSongs() != null) {
            for (String song1 : bun.getSongs()) {

                if (song1.equals(song)) {
                    ok = 0;
                    break;
                }
            }
        }
        int ok1 = 0;
        for (SongInput music : library.getSongs()) {
            if (music.getName().equals(song)) {
                ok1 = 1;
                break;
            }
        }
        if (ok1 == 0) {
            return 2;
        }
        if (ok == 1) {
            bun.addSong(song);
            return 1;
        }
        bun.removeSong(song);
        return 0;
    }

    /**
     * @param filePathInput for input file
     *
     * @throws IOException in case of exceptions to reading / writing
     */
    private static List<Command> readCommandsFromFile(final String filePathInput)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Command> commands = new ArrayList<>();

        Command[] commandArray = objectMapper.readValue(new File(filePathInput), Command[].class);

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
            }
        }
        return commands;
    }
}
