package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

@Getter
public class PlayPauseCommand extends Command {
    private int paused;

    /**
     *
     * @param playPauseCommand is the command
     *
     */
    public static void performPlayPause(final PlayPauseCommand playPauseCommand) {
        if (playPauseCommand.paused == 0) {
            playPauseCommand.paused = 1;
        } else {
            playPauseCommand.paused = 0;
        }
    }

    public PlayPauseCommand() {
    }

    public void setPaused(final int paused) {
        this.paused = paused;
    }

    /**
     *
     * @param playPauseCommand is the command
     *
     */
    public static ObjectNode createPlayPauseOutput(final PlayPauseCommand playPauseCommand) {
        ObjectNode playPauseOutput = JsonNodeFactory.instance.objectNode();
        playPauseOutput.put("command", "playPause");
        playPauseOutput.put("user", playPauseCommand.getUsername());
        playPauseOutput.put("timestamp", playPauseCommand.getTimestamp());

        if (playPauseCommand.paused == 0) {
            playPauseOutput.put("message", "Playback paused successfully.");
        } else {
            playPauseOutput.put("message", "Playback resumed successfully.");
        }
        return playPauseOutput;
    }
}
