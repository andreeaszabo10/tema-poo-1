package main;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CreatePlaylistCommand extends Command {
    private String playlistName;

    public CreatePlaylistCommand() {
    }

    @Override
    public String getPlaylistName() {
        return playlistName;
    }

    @Override
    public final void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    /**
     *
     * @param create is the command
     * @param var is a variable that indicates the message that should be printed
     */
    public static ObjectNode createPlaylistOutput(final CreatePlaylistCommand create,
                                                  final int var) {
        ObjectNode createPlaylistOutput = JsonNodeFactory.instance.objectNode();
        createPlaylistOutput.put("command", "createPlaylist");
        createPlaylistOutput.put("user", create.getUsername());
        createPlaylistOutput.put("timestamp", create.getTimestamp());
        if (var == 0) {
            createPlaylistOutput.put("message", "Playlist created successfully.");
        } else {
            createPlaylistOutput.put("message", "A playlist with the same name already exists.");
        }
        return createPlaylistOutput;
    }
}
