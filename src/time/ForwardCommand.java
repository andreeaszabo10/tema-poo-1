package time;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import main.Command;
import main.PlayerStatus;

public class ForwardCommand extends Command {
    public ForwardCommand() {
    }

    private static final int SECOND = 90;

    /**
     * go forward 90 seconds
     */
    public static void forward(final PlayerStatus playerStatus, final boolean loaded) {
        assert playerStatus.getType() != null;
        if (playerStatus.getType().equals("podcast")) {
            playerStatus.setRemainedTime(playerStatus.getRemainedTime() - SECOND);
        }
        if (!loaded) {
            playerStatus.setRemainedTime(0);
            playerStatus.setCurrentTrack("");
            playerStatus.setPaused(true);
            playerStatus.setRepeatMode(0);
            playerStatus.setShuffleMode(false);
        }
    }


    /**
     * create the output for the command
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
