// Created: 12 Aug. 2024
package de.freese.player.core.input;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

import de.freese.player.core.model.AudioCodec;
import de.freese.player.core.util.PlayerUtils;

/**
 * @author Thomas Freese
 */
public interface AudioSource {
    String getAlbum();

    String getArtist();

    default AudioCodec getAudioCodec() {
        final String fileExtension = PlayerUtils.getFileExtension(getUri());

        return AudioCodec.getByExtension(fileExtension);
    }

    /**
     * kb/s
     */
    int getBitRate();

    /**
     * 1=mono, 2=stereo
     */
    int getChannels();

    String getDisc();

    Duration getDuration();

    String getFormat();

    String getGenre();

    /**
     * varchar(2000), 8000 max.
     */
    String getMetaData();

    int getPlayCount();

    String getReleaseDate();

    /**
     * Hz
     */
    int getSampleRate();

    String getTitle();

    // Integer getSeekTime();

    Path getTmpFile();

    String getTrack();

    URI getUri();

    // /**
    //  * If 256 no volume change will be performed.
    //  */
    // Integer getVolume();

    void incrementPlayCount();

    boolean isCompilation();

    void setTmpFile(Path tmpFile);
}
