package main;

import lombok.Getter;

@Getter
public class Playlist {
    private String owner;
    private String playlistName;
    @Getter
    private String[] songs;

    public Playlist() {
        this.songs = new String[0];
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public void setUsername(String owner) {
        this.owner = owner;
    }

    public void setSongs(String[] songs) {
        this.songs = songs;
    }
    public void addSong(String song) {
        String[] newSongs = new String[songs.length + 1];
        System.arraycopy(songs, 0, newSongs, 0, songs.length);
        newSongs[songs.length] = song;
        songs = newSongs;
    }

    public void removeSong(String song) {
        String[] newSongs = new String[songs.length - 1];
        int index = 0;
        for (String s : songs) {
            if (!s.equals(song)) {
                newSongs[index++] = s;
            }
        }
        songs = newSongs;
    }
    public static Playlist performCreatePlaylist(CreatePlaylistCommand createPlaylistCommand) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setPlaylistName(createPlaylistCommand.getPlaylistName());
        newPlaylist.setUsername(createPlaylistCommand.getUsername());
        return newPlaylist;
    }
}
