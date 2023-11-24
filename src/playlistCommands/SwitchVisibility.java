package playlistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Command;
import main.Main;
import main.Playlist;

import java.util.List;

public class SwitchVisibility extends Command {
    public SwitchVisibility() {
    }

    public static Playlist switchVisibility(final List<Playlist> playlists,
                                            final SwitchVisibility swap) {
        Playlist playlist;
        int count = 0;
        for (Playlist p : playlists) {
            if (p.getOwner().equals(swap.getUsername())) {
                count++;
            }
        }
        if (swap.getPlaylistId() <= count) {
            playlist = Main.findPlaylist(playlists, swap.getUsername(), swap.getPlaylistId());
            if (playlist.getVisibility().equals("public")) {
                playlist.setVisibility("private");
            } else {
                playlist.setVisibility("public");
            }
        } else {
            playlist = null;
        }
        return playlist;
    }

    /**
     *
     */
    public static ObjectNode createSwitchOutput(final SwitchVisibility switchVisibility,
                                                final Playlist playlist) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "switchVisibility");
        output.put("user", switchVisibility.getUsername());
        output.put("timestamp", switchVisibility.getTimestamp());
        if (playlist == null) {
            output.put("message", "The specified playlist ID is too high.");
        } else if (playlist.getVisibility().equals("private")) {
            output.put("message", "Visibility status updated successfully to private.");
        } else {
            output.put("message", "Visibility status updated successfully to public.");
        }
        return output;
    }
}
