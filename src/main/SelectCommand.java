package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.List;

@Getter
public class SelectCommand extends Command{
    private String username;
    private int itemNumber;

    public SelectCommand() {
    }

    public SelectCommand(int timestamp, String username, int itemNumber) {
        super(timestamp);
        this.username = username;
        this.itemNumber = itemNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public static String performSelect(List<String> searchResults, SelectCommand selectCommand) {
        int itemNumber = selectCommand.getItemNumber();

        if (!searchResults.isEmpty()) {
            if (itemNumber >= 1 && itemNumber <= searchResults.size() && itemNumber <= 5) {
                return searchResults.get(itemNumber - 1);
            } else {
                return "1";
            }
        }
        return "2";
    }

    public static ObjectNode createSelectOutput(SelectCommand selectCommand, String selectedTrack) {
        ObjectNode selectOutput = JsonNodeFactory.instance.objectNode();
        selectOutput.put("command", "select");
        selectOutput.put("user", selectCommand.getUsername());
        selectOutput.put("timestamp", selectCommand.getTimestamp());
        if (selectedTrack.equals("1"))
            selectOutput.put("message", "The selected ID is too high.");
        else
            selectOutput.put("message", "Successfully selected " + selectedTrack + ".");
        return selectOutput;
    }
}
