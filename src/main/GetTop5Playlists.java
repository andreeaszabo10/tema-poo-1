package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetTop5Playlists extends Command {
    public GetTop5Playlists() {
    }

    private static final int MAX = 6;
    /**
     *
     */
    public static ObjectNode createTop5POutput(final GetTop5Playlists getTop5Playlists,
                                               final String[] playlists) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "getTop5Playlists");
        output.put("timestamp", getTop5Playlists.getTimestamp());
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        int count = 0;
        for (String playlist : playlists) {
            if (count <= MAX) {
                resultArray.add(playlist);
            }
            count++;
        }
        output.set("result", resultArray);
        return output;
    }
}
