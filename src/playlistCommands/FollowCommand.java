package playlistCommands;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Command;
import main.Main;
import main.Playlist;

import java.util.List;

public class FollowCommand extends Command {
    public FollowCommand() {
    }
    private Playlist playlist;

    public final Playlist getPlaylist() {
        return playlist;
    }

    public final void setPlaylist(final Playlist playlist) {
        this.playlist = playlist;
    }

    public static int follow(final boolean noSelect, final List<Playlist> playlists,
                             final String selectedTrack, final FollowCommand followCommand) {
        int flag = 0;
        if (noSelect) {
            followCommand.setPlaylist(Main.getPlaylistDetails(playlists, selectedTrack));
            if (followCommand.getPlaylist() != null) {
                if (followCommand.getPlaylist().getFollowers() != null) {
                    for (String user : followCommand.getPlaylist().getFollowers()) {
                        if (user.equals(followCommand.getUsername())) {
                            flag = 1;
                            break;
                        }
                    }
                }
                if (flag == 0) {
                    followCommand.getPlaylist().addFollower(followCommand.getUsername());
                } else {
                    followCommand.getPlaylist().removeFollower(followCommand.getUsername());
                }
            }
        }
        return flag;
    }

    /**
     *
     */
    public static ObjectNode createFollowOutput(final FollowCommand followCommand,
                                                final boolean select,
                                                final Playlist playlist, final int flag) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "follow");
        output.put("user", followCommand.getUsername());
        output.put("timestamp", followCommand.getTimestamp());
        if (!select) {
            output.put("message", "Please select a source before following or unfollowing.");
        } else if (playlist == null) {
            output.put("message", "The selected source is not a playlist.");
        } else if (flag == 0) {
            output.put("message", "Playlist followed successfully.");
        } else {
            output.put("message", "Playlist unfollowed successfully.");
        }
        return output;
    }
}
