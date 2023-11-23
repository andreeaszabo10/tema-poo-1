package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FollowCommand extends Command {
    public FollowCommand() {
    }
    public static ObjectNode createFollowOutput(final FollowCommand followCommand) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "follow");
        output.put("user", followCommand.getUsername());
        output.put("timestamp", followCommand.getTimestamp());
        output.put("message", "Playlist followed successfully.");
        return output;
    }
}
