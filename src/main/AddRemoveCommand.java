package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AddRemoveCommand extends Command {
    public int playlistId;

    public AddRemoveCommand() {
    }

    @Override
    public int getPlaylistId() {
        return playlistId;
    }

    @Override
    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
    public static ObjectNode createAddRemoveInPlaylistOutput(AddRemoveCommand addRemoveCommand, int x) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "addRemoveInPlaylist");
        output.put("user", addRemoveCommand.getUsername());
        output.put("timestamp", addRemoveCommand.getTimestamp());

        if (x == 1)
            output.put("message", "Successfully added to playlist.");
        else if (x == 0)
            output.put("message", "Successfully removed from playlist.");
        else if (x == 100)
            output.put("message", "The specified playlist does not exist.");
        else if (x == 2)
            output.put("message", "The loaded source is not a song.");
        else
            output.put("message", "Please load a source before adding to or removing from the playlist.");
        return output;
    }
}
