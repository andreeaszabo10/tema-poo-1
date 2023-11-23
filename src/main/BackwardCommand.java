package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BackwardCommand extends Command {
    public BackwardCommand() {
    }

    /**
     *
     */
    public static ObjectNode createBackwardOutput(final BackwardCommand backwardCommand,
                                                        final PlayerStatus player,
                                                  final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "backward");
        output.put("user", backwardCommand.getUsername());
        output.put("timestamp", backwardCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please select a source before rewinding.");
        } else if (!player.getType().equals("podcast")) {
            output.put("message", "The loaded source is not a podcast.");
        } else {
            output.put("message", "Rewound successfully.");
        }
        return output;
    }
}
