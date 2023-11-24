package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.EpisodeInput;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.List;

public class PrevCommand extends Command {
    public PrevCommand() {
    }

    public static void prev(final PlayerStatus playerStatus, final LibraryInput library,
                            final List<Playlist> playlists, final String selectedTrack,
                            final PrevCommand prevCommand) {
        assert playerStatus.getType() != null;
        if (playerStatus.getType().equals("playlist")) {
            Playlist playlist = Main.getPlaylistDetails(playlists, selectedTrack);
            if (playlist != null) {
                String[] songs = playlist.getSongs();
                int flag = 0;
                for (int i = 0; i < songs.length; i++) {
                    String song = songs[i];
                    SongInput currentSong = Main.getSongDetails(library, song);
                    if (song.equals(playerStatus.getCurrentTrack())) {
                        flag = 1;
                    }
                    if (flag == 1 && i != 0) {
                        assert currentSong != null;
                        if (playerStatus.getRemainedTime() != currentSong.getDuration()) {
                            playerStatus.setRemainedTime(currentSong.getDuration());
                            playerStatus.setCurrentTrack(currentSong.getName());
                        } else {
                            song = songs[i - 1];
                            SongInput prevSong = Main.getSongDetails(library, song);
                            assert prevSong != null;
                            playerStatus.setRemainedTime(prevSong.getDuration());
                            playerStatus.setCurrentTrack(prevSong.getName());
                        }
                        playerStatus.setLastTime(prevCommand.getTimestamp());
                        break;
                    }
                }
            }
        }
    }

    /**
     *
     */
    public static ObjectNode createPrevOutput(final PrevCommand prevCommand,
                                              final PlayerStatus player, final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "prev");
        output.put("user", prevCommand.getUsername());
        output.put("timestamp", prevCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before returning to the previous track.");
        } else {
            output.put("message", "Returned to previous track successfully. The current track is "
                    + player.getCurrentTrack() + ".");
        }
        return output;
    }
}
