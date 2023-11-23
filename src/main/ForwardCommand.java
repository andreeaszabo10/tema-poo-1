package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ForwardCommand extends Command {
    public ForwardCommand() {
    }

    /**
     *
     */
    public static ObjectNode createForwardOutput(final ForwardCommand forwardCommand,
                                                       final PlayerStatus player,
                                                 final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "forward");
        output.put("user", forwardCommand.getUsername());
        output.put("timestamp", forwardCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before attempting to forward.");
        } else if (!player.getType().equals("podcast")) {
            output.put("message", "The loaded source is not a podcast.");
        } else {
            output.put("message", "Skipped forward successfully.");
        }
        return output;
    }
}
