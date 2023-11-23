package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class PrevCommand extends Command {
    public PrevCommand() {
    }

    /**
     *
     */
    public static ObjectNode createPrevOutput(final PrevCommand prevCommand,
                                              final PlayerStatus player, final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "prev");
        output.put("user", prevCommand.getUsername());
        output.put("timestamp", prevCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before returning to the previous track.");
        } else {
            output.put("message", "Returned to previous track successfully. The current track is "
                    + player.getCurrentTrack() + ".");
        }
        return output;
    }
}
