package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StatusCommand extends Command {
    public StatusCommand() {
    }


    /**
     *
     * @param status is the command
     * @param player is the current state of the player
     */
    public static ObjectNode createStatus(final StatusCommand status, final PlayerStatus player) {
        ObjectNode statusOutput = JsonNodeFactory.instance.objectNode();
        statusOutput.put("command", "status");
        statusOutput.put("user", status.getUsername());
        statusOutput.put("timestamp", status.getTimestamp());

        statusOutput.putObject("stats")
                .put("name", player.getCurrentTrack())
                .put("remainedTime", player.getRemainedTime())
                .put("repeat", player.getRepeatMode())
                .put("shuffle", player.isShuffleMode())
                .put("paused", player.isPaused());

        return statusOutput;
    }
}
