package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.input.LibraryInput;
import fileio.input.PodcastInput;
import fileio.input.SongInput;

import java.util.ArrayList;
import java.util.List;

public class SearchCommand extends Command{
    private String username;
    private String type;
    private JsonNode filters;

    public SearchCommand() {
    }

    public SearchCommand(int timestamp, String username, String type, JsonNode filters) {
        super(timestamp);
        this.username = username;
        this.type = type;
        this.filters = filters;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JsonNode getFilters() {
        return filters;
    }

    public void setFilters(JsonNode filters) {
        this.filters = filters;
    }

    public static List<String> performSearch(LibraryInput library, SearchCommand searchCommand, List<Playlist> playlists) {
        List<String> searchResults = new ArrayList<>();
        String type = searchCommand.getType();

        if ("song".equals(type)) {
            JsonNode nameFilterNode = searchCommand.getFilters().get("name");
            JsonNode albumFilterNode = searchCommand.getFilters().get("album");
            JsonNode tagsFilterNode = searchCommand.getFilters().get("tags");
            JsonNode lyricsFilterNode = searchCommand.getFilters().get("lyrics");
            JsonNode genreFilterNode = searchCommand.getFilters().get("genre");
            JsonNode releaseYearFilterNode = searchCommand.getFilters().get("releaseYear");
            JsonNode artistFilterNode = searchCommand.getFilters().get("artist");
            int count = 1;
            for (SongInput song : library.getSongs()) {
                boolean match = true;

                if (nameFilterNode != null) {
                    String nameFilter = nameFilterNode.asText().toLowerCase();
                    String songName = song.getName().toLowerCase();
                    if (!songName.startsWith(nameFilter)) {
                        match = false;
                    }
                }

                if (albumFilterNode != null && !song.getAlbum().equals(albumFilterNode.asText())) {
                    match = false;
                }

                if (tagsFilterNode != null) {
                    boolean tagsMatch = false;
                    for (JsonNode tag : tagsFilterNode) {
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

                if (lyricsFilterNode != null && !song.getLyrics().contains(lyricsFilterNode.asText())) {
                    match = false;
                }

                if (genreFilterNode != null && !song.getGenre().equalsIgnoreCase(genreFilterNode.asText())) {
                    match = false;
                }

                if (releaseYearFilterNode != null) {
                    int releaseYear = song.getReleaseYear();
                    char first = releaseYearFilterNode.asText().charAt(0);
                    String numericPart = releaseYearFilterNode.asText().replaceAll("[^\\d]", "");

                    int year = Integer.parseInt(numericPart);
                    if ((first == '>') && (releaseYear <= year)) {
                        match = false;
                    } else if ((first == '<') && (releaseYear >= year)) {
                        match = false;
                    }
                }

                if (artistFilterNode != null && !song.getArtist().equals(artistFilterNode.asText())) {
                    match = false;
                }

                if (match && count < 6) {
                    searchResults.add(song.getName());
                    count++;
                }

            }
        } else if ("podcast".equals(type)) {
            JsonNode nameFilterNode = searchCommand.getFilters().get("name");
            JsonNode ownerFilterNode = searchCommand.getFilters().get("owner");
            int count = 1;
            for (PodcastInput podcast : library.getPodcasts()) {
                boolean match = nameFilterNode == null || podcast.getName().contains(nameFilterNode.asText());

                if (ownerFilterNode != null && !podcast.getOwner().equals(ownerFilterNode.asText())) {
                    match = false;
                }

                if (match && count < 6) {
                    searchResults.add(podcast.getName());
                    count++;
                }
            }
        } else if ("playlist".equals(type)){
            JsonNode ownerFilterNode = searchCommand.getFilters().get("owner");
            if (ownerFilterNode != null) {
                for (Playlist playlist : playlists) {
                    if (playlist.getOwner().equals(ownerFilterNode.asText())) {
                        searchResults.add(playlist.getPlaylistName());
                    }
                }
            }

        }

        return searchResults;
    }
    public static ObjectNode createSearchOutput(SearchCommand searchCommand, List<String> searchResults) {
        ObjectNode searchOutput = JsonNodeFactory.instance.objectNode();
        searchOutput.put("command", "search");
        searchOutput.put("user", searchCommand.getUsername());
        searchOutput.put("timestamp", searchCommand.getTimestamp());
        searchOutput.put("message", "Search returned " + searchResults.size() + " results");
        ArrayNode resultsArray = searchOutput.putArray("results");
        searchResults.forEach(resultsArray::add);
        return searchOutput;
    }
}
