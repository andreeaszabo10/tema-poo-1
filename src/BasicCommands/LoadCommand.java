package BasicCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.Command;
import main.Main;
import main.PlayerStatus;
import main.Playlist;

import java.util.ArrayList;
import java.util.List;

@Getter
public class LoadCommand extends Command {
    private String username;
    private int timestamp;
    @Getter
    private int alreadyLoaded;

    public LoadCommand() {
    }

    public final void setAlreadyLoaded(final int alreadyLoaded) {
        this.alreadyLoaded = alreadyLoaded;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * message function
     */
    public static String load(final String selectedTrack) {

        if (selectedTrack == null) {
            return "Please select a source before attempting to load.";
        }
        return "Playback loaded successfully.";
    }

    /**
     *
     *
     *
     */
    public static void loader(final PlayerStatus playerStatus, final LibraryInput library,
                              PlayerStatus back, final String selectedTrack,
                              final List<Playlist> playlists, final List<PodcastInput>
                                      loadedPodcasts, final LoadCommand loadCommand) {
        SongInput song = Main.getSongDetails(library, selectedTrack);
        PodcastInput podcast = Main.getPodcastDetails(library, selectedTrack);
        Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
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
            loadCommand.setAlreadyLoaded(0);
            for (PodcastInput pod : loadedPodcasts) {
                if (pod.getName().equals(podcast.getName())) {
                    loadCommand.setAlreadyLoaded(1);
                    break;
                }
            }
            if (loadCommand.getAlreadyLoaded() == 1) {
                playerStatus.setRemainedTime(back.getRemainedTime());
                playerStatus.setCurrentTrack(first.getName());
                playerStatus.setPaused(false);
                playerStatus.setRepeatMode(0);
                playerStatus.setShuffleMode(false);
                playerStatus.setType("podcast");
                playerStatus.setIndex(0);
                //back = playerStatus;
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
                song = Main.getSongDetails(library, songs[0]);
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

    /**
     *
     * @param loadCommand is the command
     * @param message is the message that should be printed
     */
    public static ObjectNode createLoadOutput(final LoadCommand loadCommand, final String message,
                                              final boolean select) {
        ObjectNode loadOutput = JsonNodeFactory.instance.objectNode();
        loadOutput.put("command", "load");
        loadOutput.put("user", loadCommand.getUsername());
        loadOutput.put("timestamp", loadCommand.getTimestamp());
        if (select) {
            loadOutput.put("message", message);
        } else {
            loadOutput.put("message", "Please select a source before attempting to load.");
        }
        return loadOutput;
    }
}
