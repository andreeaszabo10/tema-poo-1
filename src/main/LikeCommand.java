package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LikeCommand extends Command {
    public LikeCommand() {
    }
    public static ObjectNode createLikeOutput(LikeCommand likeCommand, boolean var, boolean loaded) {
        ObjectNode likeOutput = JsonNodeFactory.instance.objectNode();
        likeOutput.put("command", "like");
        likeOutput.put("user", likeCommand.getUsername());
        likeOutput.put("timestamp", likeCommand.getTimestamp());
        if (!loaded)
            likeOutput.put("message", "Please load a source before liking or unliking.");
        else if (!var)
            likeOutput.put("message", "Like registered successfully.");
        else
            likeOutput.put("message", "Unlike registered successfully.");
        return likeOutput;
    }

    public static String performLike(boolean var, String selected) {
        if (!var) {
            return selected;
        } else {
            return null;
        }
    }
}
