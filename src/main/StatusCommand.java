package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class StatusCommand extends Command{
    public String username;
    public StatusCommand() {
    }

    public StatusCommand(int timestamp, String username) {
        super(timestamp);
        setCommand("status");
        setUsername(username);
    }
    public static ObjectNode createStatusOutput(StatusCommand statusCommand, PlayerStatus player) {
        ObjectNode statusOutput = JsonNodeFactory.instance.objectNode();
        statusOutput.put("command", "status");
        statusOutput.put("user", statusCommand.getUsername());
        statusOutput.put("timestamp", statusCommand.getTimestamp());

        statusOutput.putObject("stats")
                .put("name", player.getCurrentTrack())
                .put("remainedTime", player.getRemainedTime())
                .put("repeat", player.getRepeatMode())
                .put("shuffle", player.isShuffleMode())
                .put("paused", player.isPaused());

        return statusOutput;
    }
}
