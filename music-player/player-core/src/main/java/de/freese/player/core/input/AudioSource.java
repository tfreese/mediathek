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
    int getSamplingRate();

    // Integer getSeekTime();

    String getTitle();

    Path getTmpFile();

    String getTrack();

    // /**
    //  * If 256 no volume change will be performed.
    //  */
    // Integer getVolume();

    URI getUri();

    boolean isCompilation();

    void setTmpFile(Path tmpFile);
}
