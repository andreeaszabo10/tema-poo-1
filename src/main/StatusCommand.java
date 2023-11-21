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
        String repeat = null;
        if (player.getRepeatMode() == 0) {
            repeat = "No Repeat";
        }
        if (player.getRepeatMode() == 1 && (player.getType().equals("podcast")
                || player.getType().equals("playlist"))) {
            repeat = "Repeat All";
        }
        if (player.getRepeatMode() == 1 && player.getType().equals("song")) {
            repeat = "Repeat Once";
        }
        if (player.getRepeatMode() == 2 && player.getType().equals("song")) {
            repeat = "Repeat Infinite";
        }
        if (player.getRepeatMode() == 2 && (player.getType().equals("podcast") || player.getType().equals("playlist"))) {
            repeat = "Repeat Current Song";
        }
        statusOutput.putObject("stats")
                .put("name", player.getCurrentTrack())
                .put("remainedTime", player.getRemainedTime())
                .put("repeat", repeat)
                .put("shuffle", player.isShuffleMode())
                .put("paused", player.isPaused());

        return statusOutput;
    }
}
