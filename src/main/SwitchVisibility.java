package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SwitchVisibility extends Command {
    public SwitchVisibility() {
    }

    /**
     *
     */
    public static ObjectNode createSwitchOutput(final SwitchVisibility switchVisibility,
                                                final Playlist playlist) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "switchVisibility");
        output.put("user", switchVisibility.getUsername());
        output.put("timestamp", switchVisibility.getTimestamp());
        if (playlist == null) {
            output.put("message", "The specified playlist ID is too high.");
        } else if (playlist.getVisibility().equals("private")) {
            output.put("message", "Visibility status updated successfully to private.");
        } else {
            output.put("message", "Visibility status updated successfully to public.");
        }
        return output;
    }
}
