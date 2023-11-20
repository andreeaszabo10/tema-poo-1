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
        int lasttimestamp = 0;
        List<Playlist> playlists = new ArrayList<>();
        boolean ok = false;
        List<String> liked = new ArrayList<>();
        boolean loaded = false;
        String load = null;

        for (Command command : commands) {
            if (command instanceof SearchCommand searchCommand) {
                loaded = false;
                searchResults = SearchCommand.performSearch(library, searchCommand, playlists);
                outputs.add(SearchCommand.createSearchOutput(searchCommand, searchResults));
            }
            if (command instanceof SelectCommand selectCommand) {
                selectedTrack = SelectCommand.performSelect(searchResults, selectCommand);
                outputs.add(SelectCommand.createSelectOutput(selectCommand, selectedTrack));
            }
            if (command instanceof LoadCommand loadCommand) {
                String message = LoadCommand.performLoad(selectedTrack, loadCommand);
                lasttimestamp = loadCommand.timestamp;
                SongInput song;
                PodcastInput podcast;
                ok = false;
                loaded = true;
                if (selectedTrack != null) {
                    song = getSongDetails(library, selectedTrack);
                    podcast = getPodcastDetails(library, selectedTrack);
                    if (song != null) {
                        playerStatus.setRemainedTime(song.getDuration());
                        playerStatus.setCurrentTrack(song.getName());
                        playerStatus.setPaused(false);
                        playerStatus.setRepeatMode("No Repeat");
                        playerStatus.setShuffleMode(false);
                    }
                    if (podcast != null) {
                        ArrayList<EpisodeInput> list = podcast.getEpisodes();
                        EpisodeInput first = list.get(0);
                        playerStatus.setRemainedTime(first.getDuration());
                        playerStatus.setCurrentTrack(first.getName());
                        playerStatus.setPaused(false);
                        playerStatus.setRepeatMode("No Repeat");
                        playerStatus.setShuffleMode(false);
                    }
                }
                outputs.add(LoadCommand.createLoadOutput(loadCommand, message));
            }
            if (command instanceof StatusCommand statusCommand) {
                if (!playerStatus.isPaused()) {
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime() - statusCommand.timestamp + lasttimestamp);
                }
                lasttimestamp = statusCommand.timestamp;
                if (statusCommand.timestamp > lasttimestamp + playerStatus.getRemainedTime()) {
                    playerStatus.setRemainedTime(0);
                    playerStatus.setCurrentTrack("");
                    playerStatus.setPaused(true);
                    playerStatus.setRepeatMode("No Repeat");
                    playerStatus.setShuffleMode(false);
                }
                outputs.add(StatusCommand.createStatusOutput(statusCommand, playerStatus));
            }
            if (command instanceof PlayPauseCommand playPauseCommand) {
                if (playerStatus.isPaused()) {
                    playPauseCommand.paused = 0;
                    lasttimestamp = playPauseCommand.timestamp;
                    playerStatus.setPaused(false);
                } else {
                    playPauseCommand.paused = 1;
                    playerStatus.setRemainedTime(playerStatus.getRemainedTime() - playPauseCommand.timestamp + lasttimestamp);
                    lasttimestamp = playPauseCommand.timestamp;
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
                if (var == 0)
                    playlists.add(playlist);

                outputs.add(CreatePlaylistCommand.createPlaylistOutput(createPlaylistCommand, var));
            }
            if (command instanceof AddRemoveCommand addRemoveCommand) {
                int x;
                if (loaded)
                    x = performAddRemove(addRemoveCommand, playlists, selectedTrack, library);
                else
                    x = 3;
                outputs.add(AddRemoveCommand.createAddRemoveInPlaylistOutput(addRemoveCommand, x));
            }
            if (command instanceof LikeCommand likeCommand) {
                String a = LikeCommand.performLike(ok, selectedTrack);
                int var = 0;
                if (a != null) {
                    for (String x : liked) {
                        if ( a.equals(x)) {
                            var = 1;
                            break;
                        }
                    }
                    if (var == 0)
                        liked.add(a);
                    else
                        liked.remove(a);
                }
                outputs.add(LikeCommand.createLikeOutput(likeCommand, ok, loaded));
                ok = !ok;
            }
            if (command instanceof ShowPlaylistsCommand showPlaylistsCommand) {
                outputs.add(ShowPlaylistsCommand.createShowPlaylistsOutput(showPlaylistsCommand, playlists));
            }
            if (command instanceof ShowSongsCommand showSongsCommand) {
                outputs.add(ShowSongsCommand.createShowSongsOutput(showSongsCommand, liked));
            }
        }

        ObjectWriter objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
        objectWriter.writeValue(new File(filePathOutput), outputs);

    }

    private static SongInput getSongDetails(LibraryInput library, String trackName) {
        for (SongInput song : library.getSongs()) {
            if (song.getName().equals(trackName)) {
                return song;
            }
        }
        return null;
    }
    private static PodcastInput getPodcastDetails(LibraryInput library, String trackName) {
        for (PodcastInput podcast : library.getPodcasts()) {
            if (podcast.getName().equals(trackName)) {
                return podcast;
            }
        }
        return null;
    }
    private static int performAddRemove(AddRemoveCommand addRemoveCommand, List<Playlist> playlists, String song, LibraryInput library) {
        if (addRemoveCommand.playlistId > playlists.size())
            return 100;
        Playlist bun = playlists.get(addRemoveCommand.playlistId - 1);
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
        if (ok1 == 0)
            return 2;
        if (ok == 1) {
            bun.addSong(song);
            return 1;
        }
        bun.removeSong(song);
        return 0;
    }
    private static List<Command> readCommandsFromFile(String filePathInput) throws IOException {
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
