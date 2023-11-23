package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FollowCommand extends Command {
    public FollowCommand() {
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
