package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class NextCommand extends Command {
    public NextCommand() {
    }

    /**
     *
     */
    public static ObjectNode createNextOutput(final NextCommand nextCommand,
                                              final PlayerStatus player,
                                              final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "next");
        output.put("user", nextCommand.getUsername());
        output.put("timestamp", nextCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before skipping to the next track.");
        } else {
            output.put("message", "Skipped to next track successfully. The current track is "
                    + player.getCurrentTrack() + ".");
        }
        return output;
    }
}
