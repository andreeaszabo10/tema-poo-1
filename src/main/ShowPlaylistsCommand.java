package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class ShowPlaylistsCommand extends Command {

    /**
     *
     * @param show is the command
     * @param playlists is the list of playlists
     */
    public static ObjectNode createShowPlaylistsOutput(final ShowPlaylistsCommand show,
                                                       final List<Playlist> playlists) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();

        for (Playlist playlist : playlists) {
            if (show.getUsername().equals(playlist.getOwner())) {
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
        output.put("user", show.getUsername());
        output.put("timestamp", show.getTimestamp());
        output.putArray("result").addAll(resultArray);

        return output;
    }
}
