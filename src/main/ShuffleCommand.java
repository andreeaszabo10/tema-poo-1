package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ShuffleCommand extends Command {
    private int seed;
    public ShuffleCommand() {
    }

    @Override
    public final int getSeed() {
        return seed;
    }

    @Override
    public final void setSeed(final int seed) {
        this.seed = seed;
    }

    /**
     *
     */
    public static ObjectNode createShuffleOutput(final ShuffleCommand shuffleCommand,
                                                 final PlayerStatus player, final boolean loaded) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "shuffle");
        output.put("user", shuffleCommand.getUsername());
        output.put("timestamp", shuffleCommand.getTimestamp());
        if (!loaded || player.getRemainedTime() < 0) {
            output.put("message", "Please load a source before using the shuffle function.");
        } else if (!player.getType().equals("playlist")) {
            output.put("message", "The loaded source is not a playlist.");
        } else if (player.isShuffleMode()) {
            output.put("message", "Shuffle function activated successfully.");
        } else {
            output.put("message", "Shuffle function deactivated successfully.");
        }
        return output;
    }
}
