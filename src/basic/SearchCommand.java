package basic;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;
import lombok.Getter;
import main.Command;
import main.Playlist;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SearchCommand extends Command {
    private String username;
    private String type;
    private JsonNode filters;

    public SearchCommand() {
    }

    public final void setUsername(final String username) {
        this.username = username;
    }

    public final void setType(final String type) {
        this.type = type;
    }

    public final void setFilters(final JsonNode filters) {
        this.filters = filters;
    }

    private static final int MAX = 6;

    /**
     *
     */
    public static List<String> search(final LibraryInput library,
                                             final SearchCommand searchCommand,
                                             final List<Playlist> playlists) {
        List<String> searchResults = new ArrayList<>();
        String type = searchCommand.getType();

        if ("song".equals(type)) {
            JsonNode name = searchCommand.getFilters().get("name");
            JsonNode album = searchCommand.getFilters().get("album");
            JsonNode tags = searchCommand.getFilters().get("tags");
            JsonNode lyrics = searchCommand.getFilters().get("lyrics");
            JsonNode genre = searchCommand.getFilters().get("genre");
            JsonNode release = searchCommand.getFilters().get("releaseYear");
            JsonNode artist = searchCommand.getFilters().get("artist");
            int count = 1;
            for (SongInput song : library.getSongs()) {
                boolean match = true;

                if (name != null) {
                    String nameFilter = name.asText().toLowerCase();
                    String songName = song.getName().toLowerCase();
                    if (!songName.startsWith(nameFilter)) {
                        match = false;
                    }
                }

                if (album != null && !song.getAlbum().equals(album.asText())) {
                    match = false;
                }

                if (tags != null) {
                    boolean tagsMatch = false;
                    for (JsonNode tag : tags) {
                        if (song.getTags().contains(tag.asText())) {
                            tagsMatch = true;
                        } else {
                            tagsMatch = false;
                            break;
                        }
                    }
                    if (!tagsMatch) {
                        match = false;
                    }
                }

                if (lyrics != null && !song.getLyrics()
                        .toLowerCase().contains(lyrics.asText().toLowerCase())) {
                    match = false;
                }

                if (genre != null && !song.getGenre().equalsIgnoreCase(genre.asText())) {
                    match = false;
                }

                if (release != null) {
                    int releaseYear = song.getReleaseYear();
                    char first = release.asText().charAt(0);
                    String num;
                    num = release.asText().replaceAll("[^\\d]", "");

                    int year = Integer.parseInt(num);
                    if ((first == '>') && (releaseYear <= year)) {
                        match = false;
                    } else if ((first == '<') && (releaseYear >= year)) {
                        match = false;
                    }
                }

                if (artist != null && !song.getArtist().equals(artist.asText())) {
                    match = false;
                }

                if (match && count < MAX) {
                    searchResults.add(song.getName());
                    count++;
                }

            }
        } else if ("podcast".equals(type)) {
            JsonNode nameFilterNode = searchCommand.getFilters().get("name");
            JsonNode ownerFilterNode = searchCommand.getFilters().get("owner");
            int count = 1;
            for (PodcastInput podcast : library.getPodcasts()) {
                boolean match = nameFilterNode == null
                        || podcast.getName().contains(nameFilterNode.asText());

                if (ownerFilterNode != null
                        && !podcast.getOwner().equals(ownerFilterNode.asText())) {
                    match = false;
                }

                if (match && count < MAX) {
                    searchResults.add(podcast.getName());
                    count++;
                }
            }
        } else if ("playlist".equals(type)) {
            JsonNode ownerFilterNode = searchCommand.getFilters().get("owner");
            JsonNode nameFilterNode = searchCommand.getFilters().get("name");
            if (ownerFilterNode != null) {
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(ownerFilterNode.asText())) {
                        if (playlist.getVisibility().equals("public")
                                || playlist.getOwner().equals(searchCommand.getUsername())) {
                            searchResults.add(playlist.getPlaylistName());
                        }
                    }
                }
            }
            if (nameFilterNode != null) {
                for (Playlist playlist : playlists) {
                    if (playlist.getPlaylistName().equals(nameFilterNode.asText())) {
                        if (playlist.getVisibility().equals("public")
                                || playlist.getOwner().equals(searchCommand.getUsername())) {
                            searchResults.add(playlist.getPlaylistName());
                        }
                    }
                }
            }

        }
        return searchResults;
    }

    /**
     *
     * @param searchCommand is the command
     * @param searchResults is the list of resulted songs
     */
    public static ObjectNode createSearchOutput(final SearchCommand searchCommand,
                                                final List<String> searchResults) {
        ObjectNode out = JsonNodeFactory.instance.objectNode();
        out.put("command", "search");
        out.put("user", searchCommand.getUsername());
        out.put("timestamp", searchCommand.getTimestamp());
        out.put("message", "Search returned " + searchResults.size() + " results");
        ArrayNode resultsArray = out.putArray("results");
        searchResults.forEach(resultsArray::add);
        return out;
    }
}
