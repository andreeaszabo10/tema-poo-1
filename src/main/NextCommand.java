package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.List;

public class NextCommand extends Command {
    public NextCommand() {
    }

    public static void next(final PlayerStatus playerStatus, final LibraryInput library,
                            final List<Playlist> playlists, final String selectedTrack,
                            final NextCommand nextCommand) {
        assert playerStatus.getType() != null;
        if (playerStatus.getType().equals("playlist")) {
            Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
            if (playlist != null) {
                String[] songs = playlist.getSongs();
                int flag = 0;
                for (String song : songs) {
                    SongInput currentSong = Main.getSongDetails(library, song);
                    if (flag == 1) {
                        assert currentSong != null;
                        playerStatus.setRemainedTime(currentSong.getDuration());
                        playerStatus.setCurrentTrack(currentSong.getName());
                        playerStatus.setLastTime(nextCommand.getTimestamp());
                        break;
                    }
                    if (song.equals(playerStatus.getCurrentTrack())) {
                        flag = 1;
                    }
                }
            }
        }
        if (playerStatus.getType().equals("podcast")) {
            PodcastInput podcast = Main.getPodcastDetails(library, selectedTrack);
            if (podcast != null) {
                ArrayList<EpisodeInput> episodes = podcast.getEpisodes();
                int flag = 0;
                for (EpisodeInput episode : episodes) {
                    if (flag == 1) {
                        assert episode != null;
                        playerStatus.setRemainedTime(episode.getDuration());
                        playerStatus.setCurrentTrack(episode.getName());
                        playerStatus.setLastTime(nextCommand.getTimestamp());
                        break;
                    }
                    if (episode.getName().equals(playerStatus.getCurrentTrack())) {
                        flag = 1;
                    }
                }
            }
        }
    }

    /**
     *
     */
    public static ObjectNode createNextOutput(final NextCommand nextCommand,
                                              final PlayerStatus player,
                                              final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "next");
        output.put("user", nextCommand.getUsername());
        output.put("timestamp", nextCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before skipping to the next track.");
        } else {
            output.put("message", "Skipped to next track successfully. The current track is "
                    + player.getCurrentTrack() + ".");
        }
        return output;
    }
}
