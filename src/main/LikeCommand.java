package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LikeCommand extends Command {
    public LikeCommand() {
    }

    /**
     *
     * @param likeCommand is the command
     * @param flag is a variable that indicates the message that should be printed
     * @param loaded is used to see if we loaded a song or not
     */
    public static ObjectNode createLikeOutput(final LikeCommand likeCommand,
                                              final int flag, final boolean loaded) {
        ObjectNode likeOutput = JsonNodeFactory.instance.objectNode();
        likeOutput.put("command", "like");
        likeOutput.put("user", likeCommand.getUsername());
        likeOutput.put("timestamp", likeCommand.getTimestamp());
        if (!loaded) {
            likeOutput.put("message", "Please load a source before liking or unliking.");
        } else if (flag == 0) {
            likeOutput.put("message", "Like registered successfully.");
        } else {
            likeOutput.put("message", "Unlike registered successfully.");
        }
        return likeOutput;
    }
}
