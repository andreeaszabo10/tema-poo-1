package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GetTop5Songs extends Command {
    public GetTop5Songs() {
    }

    /**
     *
     */
    public static ObjectNode createTop5Output(final GetTop5Songs getTop5Songs) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "getTop5Songs");
        output.put("timestamp", getTop5Songs.getTimestamp());
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        output.put("result", "null");
        return output;
    }
}
