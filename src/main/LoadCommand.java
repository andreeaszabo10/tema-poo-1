package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

@Getter
public class LoadCommand extends Command {
    public String username;
    public int timestamp;

    public LoadCommand() {
    }

    public LoadCommand(String username, int timestamp) {
        this.username = username;
        this.timestamp = timestamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    public static String performLoad(String selectedTrack, LoadCommand loadCommand) {

        if (selectedTrack == null)
            return "Please select a source before attempting to load.";

        return "Playback loaded successfully.";
    }

    public static ObjectNode createLoadOutput(LoadCommand loadCommand, String message) {
        ObjectNode loadOutput = JsonNodeFactory.instance.objectNode();
        loadOutput.put("command", "load");
        loadOutput.put("user", loadCommand.getUsername());
        loadOutput.put("timestamp", loadCommand.getTimestamp());
        loadOutput.put("message", message);
        return loadOutput;
    }
}
