package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ShowSongsCommand extends Command{
    public static ObjectNode createShowSongsOutput(ShowSongsCommand command, List<String> liked) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "showPreferredSongs");
        output.put("user", command.getUsername());
        output.put("timestamp", command.getTimestamp());

        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        for (String likedSong : liked) {
            resultArray.add(JsonNodeFactory.instance.textNode(likedSong));
        }
        output.set("result", resultArray);

        return output;
    }
}
