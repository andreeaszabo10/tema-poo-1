package fileio.input;

import lombok.Getter;

import java.util.ArrayList;

public final class SongInput {
    @Getter
    private String name;
    @Getter
    private Integer duration;
    @Getter
    private String album;
    @Getter
    private ArrayList<String> tags;
    @Getter
    private String lyrics;
    @Getter
    private String genre;
    private Integer releaseYear;
    @Getter
    private String artist;

    public SongInput() {
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public void setTags(final ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setLyrics(final String lyrics) {
        this.lyrics = lyrics;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(final int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }
}
