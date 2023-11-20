package main;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class Command {
    private String command;
    private int timestamp;
    private String username;
    private String type;
    private JsonNode filters;
    private int itemNumber;
    private int seed;
    private int playlistId;
    private String playlistName;

    public Command() {
    }

    /**
     * Sets the timestamp of the command.
     * @param timestamp is the time of the execution of the command
     */
    public Command(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the username of the command.
     * @param username is the user that wants to execute the command
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Sets the name of the command.
     * @param command is the name of the command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public void setFilters(final JsonNode filters) {
        this.filters = filters;
    }

    public void setItemNumber(final int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setTimestamp(final int timestamp) {
        this.timestamp = timestamp;
    }

    public void setSeed(final int seed) {
        this.seed = seed;
    }

    public void setPlaylistId(final int playlistId) {
        this.playlistId = playlistId;
    }

    public void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }
}
