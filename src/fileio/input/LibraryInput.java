package fileio.input;

import lombok.Getter;

import java.util.ArrayList;

@Getter
public final class LibraryInput {
    private ArrayList<SongInput> songs;
    private ArrayList<PodcastInput> podcasts;
    private ArrayList<UserInput> users;

    public LibraryInput() {
    }

    public void setSongs(final ArrayList<SongInput> songs) {
        this.songs = songs;
    }

    public void setPodcasts(final ArrayList<PodcastInput> podcasts) {
        this.podcasts = podcasts;
    }

    public void setUsers(final ArrayList<UserInput> users) {
        this.users = users;
    }
}
