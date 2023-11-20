package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ShowSongsCommand extends Command {

    /**
     *
     * @param c is the command
     * @param liked is the list of liked songs
     */
    public static ObjectNode createOutput(final ShowSongsCommand c, final List<String> liked) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "showPreferredSongs");
        output.put("user", c.getUsername());
        output.put("timestamp", c.getTimestamp());

        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        for (String likedSong : liked) {
            resultArray.add(JsonNodeFactory.instance.textNode(likedSong));
        }
        output.set("result", resultArray);

        return output;
    }
}
