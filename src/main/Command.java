package main;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;

@Getter
public class Command {
    public String command;
    public int timestamp;
    public String username;
    private String type;
    private JsonNode filters;
    private int itemNumber;
    public int seed;
    public int playlistId;
    public String playlistName;

    public Command() {
    }

    public Command(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFilters(JsonNode filters) {
        this.filters = filters;
    }

    public void setItemNumber(int itemNumber) {
        this.itemNumber = itemNumber;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
}
