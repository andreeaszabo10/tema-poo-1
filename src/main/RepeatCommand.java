package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class RepeatCommand extends Command {
    public RepeatCommand() {
    }
    public static ObjectNode createRepeatOutput(final RepeatCommand repeatCommand,
                                                final int repeat, final PlayerStatus player) {
        ObjectNode repeatOutput = JsonNodeFactory.instance.objectNode();
        repeatOutput.put("command", "repeat");
        repeatOutput.put("user", repeatCommand.getUsername());
        repeatOutput.put("timestamp", repeatCommand.getTimestamp());
        if (repeat == 0) {
            repeatOutput.put("message", "Repeat mode changed to no repeat.");
        }
        if (repeat == 1 && (player.getType().equals("podcast") || player.getType().equals("playlist"))) {
            repeatOutput.put("message", "Repeat mode changed to repeat all.");
        }
        if (repeat == 1 && player.getType().equals("song")) {
            repeatOutput.put("message", "Repeat mode changed to repeat once.");
        }
        if (repeat == 2 && player.getType().equals("song")) {
            repeatOutput.put("message", "Repeat mode changed to repeat infinite.");
        }
        if (repeat == 2 && (player.getType().equals("podcast") || player.getType().equals("playlist"))) {
            repeatOutput.put("message", "Repeat mode changed to repeat current song.");
        }
        if (repeat == 3) {
            repeatOutput.put("message", "Please load a source before setting the repeat status.");
        }
        return repeatOutput;
    }
}
