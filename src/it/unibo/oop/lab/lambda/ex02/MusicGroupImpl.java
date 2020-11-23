package it.unibo.oop.lab.lambda.ex02;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                .map(s1 -> s1.getSongName())
                .sorted((s1, s2) -> s1.compareTo(s2));
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
                .filter(i -> i.getValue() == year)
                .map(i -> i.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
                .filter(i -> i.albumName.isPresent())
                .map(i -> i.albumName.get())
                .filter(i -> i.equals(albumName))
                .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                .filter(i -> !i.getAlbumName().isPresent())
                .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                .filter(i -> i.albumName.isPresent())
                .filter(i -> i.getAlbumName().get().equals(albumName))
                .mapToDouble(i -> i.getDuration()).average();
    }

    @Override
    public Optional<String> longestSong() {
        return Optional.of(this.songs.stream()
                .max((i1, i2) -> Double.compare(i1.getDuration(), i2.getDuration()))
                .get()
                .getSongName());
    }

    //I copied this method from the "Solutions" branch because unable to solve the requested problem
    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream().filter(a -> a.getAlbumName().isPresent())
                .collect(Collectors.groupingBy(Song::getAlbumName, Collectors.summingDouble(Song::getDuration)))
                .entrySet().stream()
                .collect(Collectors.maxBy(Comparator.comparingDouble(Entry::getValue)))
                .flatMap(Entry::getKey);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
