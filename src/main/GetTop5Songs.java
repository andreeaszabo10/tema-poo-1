package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.SongInput;

public class GetTop5Songs extends Command {
    public GetTop5Songs() {
    }

    private static final int MAX = 4;

    /**
     *
     */
    public static ObjectNode createTop5Output(final GetTop5Songs getTop5Songs,
                                              final SongInput[] songs) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "getTop5Songs");
        output.put("timestamp", getTop5Songs.getTimestamp());
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        int count = 0;
        for (SongInput song : songs) {
            if (count <= MAX) {
                resultArray.add(song.getName());
            }
            count++;
        }
        output.set("result", resultArray);
        return output;
    }
}
