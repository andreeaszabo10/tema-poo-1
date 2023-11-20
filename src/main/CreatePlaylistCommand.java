package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class CreatePlaylistCommand extends Command{
    public String playlistName;

    public CreatePlaylistCommand() {
    }

    @Override
    public String getPlaylistName() {
        return playlistName;
    }

    @Override
    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public static ObjectNode createPlaylistOutput(CreatePlaylistCommand createPlaylistCommand, int var) {
        ObjectNode createPlaylistOutput = JsonNodeFactory.instance.objectNode();
        createPlaylistOutput.put("command", "createPlaylist");
        createPlaylistOutput.put("user", createPlaylistCommand.getUsername());
        createPlaylistOutput.put("timestamp", createPlaylistCommand.getTimestamp());
        if (var == 0)
            createPlaylistOutput.put("message", "Playlist created successfully.");
        else
            createPlaylistOutput.put("message", "A playlist with the same name already exists.");
        return createPlaylistOutput;
    }
}
