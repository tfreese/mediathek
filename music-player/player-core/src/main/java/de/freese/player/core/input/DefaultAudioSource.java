// Created: 16 Juli 2024
package de.freese.player.core.input;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

/**
 * @author Thomas Freese
 */
public class DefaultAudioSource implements AudioSource {
    private String album;
    private String artist;
    private int bitRate;
    private int channels;
    private boolean compilation;
    private String disc;
    private Duration duration;
    private String format;
    private String genre;
    private String metaData;
    private int playCount;
    private String releaseDate;
    private int samplingRate;
    private String title;
    private Path tmpFile;
    private String track;
    private URI uri;

    @Override
    public String getAlbum() {
        return album;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public int getBitRate() {
        return bitRate;
    }

    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public String getDisc() {
        return disc;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public String getMetaData() {
        return metaData;
    }

    @Override
    public int getPlayCount() {
        return playCount;
    }

    @Override
    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int getSamplingRate() {
        return samplingRate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Path getTmpFile() {
        return tmpFile;
    }

    @Override
    public String getTrack() {
        return track;
    }

    @Override
    public URI getUri() {
        return uri;
    }

    @Override
    public void incrementPlayCount() {
        playCount++;
    }

    @Override
    public boolean isCompilation() {
        return compilation;
    }

    public void setAlbum(final String album) {
        this.album = album;
    }

    public void setArtist(final String artist) {
        this.artist = artist;
    }

    public void setBitRate(final int bitRate) {
        this.bitRate = bitRate;
    }

    public void setChannels(final int channels) {
        this.channels = channels;
    }

    public void setCompilation(final boolean compilation) {
        this.compilation = compilation;
    }

    public void setDisc(final String disc) {
        this.disc = disc;
    }

    public void setDuration(final Duration duration) {
        this.duration = duration;
    }

    public void setFormat(final String format) {
        this.format = format;
    }

    public void setGenre(final String genre) {
        this.genre = genre;
    }

    public void setMetaData(final String metaData) {
        this.metaData = metaData;
    }

    public void setPlayCount(final int playCount) {
        this.playCount = playCount;
    }

    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setSamplingRate(final int samplingRate) {
        this.samplingRate = samplingRate;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public void setTmpFile(final Path tmpFile) {
        this.tmpFile = tmpFile;
    }

    public void setTrack(final String track) {
        this.track = track;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        // return String.format(
        //         "%s format=%s, bitrate=%s, samplingrate=%s, duration=%s, channels=%s",
        //         getClass().getName(), format, bitRate, samplingRate, duration, channels);
        return getUri().toString();
    }
}
