package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AddRemoveCommand extends Command {
    private int playlistId;

    public AddRemoveCommand() {
    }

    @Override
    public final int getPlaylistId() {
        return playlistId;
    }

    @Override
    public final void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    /**
     *
     * @param addRemove is the command
     * @param x is a variable that indicates the message that should be printed
     */
    public static ObjectNode createAddRemoveOutput(final AddRemoveCommand addRemove, final int x) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "addRemoveInPlaylist");
        output.put("user", addRemove.getUsername());
        output.put("timestamp", addRemove.getTimestamp());

        if (x == 1) {
            output.put("message", "Successfully added to playlist.");
        } else if (x == 0) {
            output.put("message", "Successfully removed from playlist.");
        } else if (x == -1) {
            output.put("message", "The specified playlist does not exist.");
        } else if (x == 2) {
            output.put("message", "The loaded source is not a song.");
        } else {
            output.put("message", "Please load a source "
                        + "before adding to or removing from the playlist.");
        }
        return output;
    }
}
