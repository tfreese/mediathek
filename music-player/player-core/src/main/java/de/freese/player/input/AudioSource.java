// Created: 12 Aug. 2024
package de.freese.player.input;

import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;

/**
 * @author Thomas Freese
 */
public interface AudioSource {
    String getAlbum();

    String getArtist();

    /**
     * kb/s
     */
    int getBitRate();

    /**
     * 1=mono, 2=stereo
     */
    int getChannels();

    String getDate();

    String getDisc();

    Duration getDuration();

    String getFormat();

    String getGenre();

    /**
     * varchar(2000), 8000 max.
     */
    String getMetaData();

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
