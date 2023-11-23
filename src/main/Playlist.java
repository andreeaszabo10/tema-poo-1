package main;

import lombok.Getter;

@Getter
public class Playlist {
    @Getter
    private String owner;
    private String playlistName;
    @Getter
    private String[] songs;
    @Getter
    private String[] songsNoShuffle;

    public Playlist() {
        this.songs = new String[0];
    }

    public final void setSongsNoShuffle(final String[] songsNoShuffle) {
        this.songsNoShuffle = songsNoShuffle;
    }

    public final void setPlaylistName(final String playlistName) {
        this.playlistName = playlistName;
    }

    public final void setUsername(final String owner) {
        this.owner = owner;
    }

    public final void setOwner(final String owner) {
        this.owner = owner;
    }

    public final void setSongs(final String[] songs) {
        this.songs = songs;
    }
    public final void addSong(final String song) {
        String[] newSongs = new String[songs.length + 1];
        System.arraycopy(songs, 0, newSongs, 0, songs.length);
        newSongs[songs.length] = song;
        songs = newSongs;
    }

    public final void removeSong(final String song) {
        String[] newSongs = new String[songs.length - 1];
        int index = 0;
        for (String s : songs) {
            if (!s.equals(song)) {
                newSongs[index++] = s;
            }
        }
        songs = newSongs;
    }
    public static Playlist performCreatePlaylist(final CreatePlaylistCommand createPlaylist) {
        Playlist newPlaylist = new Playlist();
        newPlaylist.setPlaylistName(createPlaylist.getPlaylistName());
        newPlaylist.setUsername(createPlaylist.getUsername());
        newPlaylist.setOwner(createPlaylist.getUsername());
        return newPlaylist;
    }
}
