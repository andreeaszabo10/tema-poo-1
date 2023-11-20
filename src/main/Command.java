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

    public Command(final int timestamp) {
        this.timestamp = timestamp;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

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
