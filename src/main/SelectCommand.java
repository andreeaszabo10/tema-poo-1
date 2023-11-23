package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Getter;

import java.util.List;

@Getter
public class SelectCommand extends Command {
    private String username;
    private int itemNumber;

    public SelectCommand() {
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public static String performSelect(final List<String> searchResults,
                                       final SelectCommand selectCommand) {
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
    /**
     *
     * @param selectCommand is the command
     * @param selected is the selected song
     */
    public static ObjectNode createSelectOutput(final SelectCommand selectCommand,
                                                final String selected, final List<String> searchResults) {
        ObjectNode selectOutput = JsonNodeFactory.instance.objectNode();
        selectOutput.put("command", "select");
        selectOutput.put("user", selectCommand.getUsername());
        selectOutput.put("timestamp", selectCommand.getTimestamp());
        if (selected.equals("1")) {
            selectOutput.put("message", "The selected ID is too high.");
        } else if (searchResults == null) {
            selectOutput.put("message", "Please conduct a search before making a selection.");
        } else {
            selectOutput.put("message", "Successfully selected " + selected + ".");
        }
        return selectOutput;
    }
}
