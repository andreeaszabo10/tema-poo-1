package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ShowPlaylistsCommand extends Command {

    public static ObjectNode createShowPlaylistsOutput(ShowPlaylistsCommand showPlaylistsCommand, List<Playlist> playlists) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();

        for (Playlist playlist : playlists) {
            if (showPlaylistsCommand.username.equals(playlist.getOwner())) {
                ObjectNode playlistNode = JsonNodeFactory.instance.objectNode();
                playlistNode.put("name", playlist.getPlaylistName());
                ArrayNode songsArray = JsonNodeFactory.instance.arrayNode();
                for (String song : playlist.getSongs()) {
                    songsArray.add(song);
                }
                playlistNode.putArray("songs").addAll(songsArray);
                playlistNode.put("visibility", "public");
                playlistNode.put("followers", 0);

                resultArray.add(playlistNode);
            }
        }

        output.put("command", "showPlaylists");
        output.put("user", showPlaylistsCommand.username);
        output.put("timestamp", showPlaylistsCommand.timestamp);
        output.putArray("result").addAll(resultArray);

        return output;
    }
}
