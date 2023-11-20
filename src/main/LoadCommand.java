package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

@Getter
public class LoadCommand extends Command {
    private String username;
    private int timestamp;

    public LoadCommand() {
    }

    public LoadCommand(final String username, final int timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public static String performLoad(final String selectedTrack) {

        if (selectedTrack == null) {
            return "Please select a source before attempting to load.";
        }
        return "Playback loaded successfully.";
    }

    /**
     *
     * @param loadCommand is the command
     * @param message is the message that should be printed
     */
    public static ObjectNode createLoadOutput(final LoadCommand loadCommand, final String message,
                                              final boolean select) {
        ObjectNode loadOutput = JsonNodeFactory.instance.objectNode();
        loadOutput.put("command", "load");
        loadOutput.put("user", loadCommand.getUsername());
        loadOutput.put("timestamp", loadCommand.getTimestamp());
        if (select)
            loadOutput.put("message", message);
        else
            loadOutput.put("message", "Please select a source before attempting to load.");
        return loadOutput;
    }
}
