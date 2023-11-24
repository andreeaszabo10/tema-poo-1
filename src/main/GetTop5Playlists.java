package main;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;

import java.util.List;

public class GetTop5Playlists extends Command {
    public GetTop5Playlists() {
    }

    private static final int MAX = 4;

    public static String[] top5Playlists(final List<Playlist> playlists) {
        String[] array = new String[playlists.size()];
        int[] followersNumber = new int[playlists.size()];
        for (int i = 0; i < playlists.size(); i++) {
            array[i] = playlists.get(i).getPlaylistName();
            followersNumber[i] = playlists.get(i).getFollowersNumber();
        }
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (followersNumber[j] < followersNumber[j + 1]) {
                    int aux = followersNumber[j];
                    followersNumber[j] = followersNumber[j + 1];
                    followersNumber[j + 1] = aux;
                    String aux1 = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = aux1;
                }
            }
        }
        return array;
    }
    /**
     *
     */
    public static ObjectNode createTop5POutput(final GetTop5Playlists getTop5Playlists,
                                               final String[] playlists) {
        ObjectNode output = JsonNodeFactory.instance.objectNode();
        output.put("command", "getTop5Playlists");
        output.put("timestamp", getTop5Playlists.getTimestamp());
        ArrayNode resultArray = JsonNodeFactory.instance.arrayNode();
        int count = 0;
        for (String playlist : playlists) {
            if (count <= MAX) {
                resultArray.add(playlist);
            }
            count++;
        }
        output.set("result", resultArray);
        return output;
    }
}
