// Created: 16 Juli 2024
package de.freese.player.core.ffmpeg;

/**
 * @author Thomas Freese
 */
public final class FFLocator {
    public static FFmpeg createFFmpeg() {
        return new DefaultFFmpeg("ffmpeg");
    }

    public static FFprobe createFFprobe() {
        return new DefaultFFprobe("ffprobe");
        // return new DefaultFFprobeRegEx("ffprobe");
    }

    private FFLocator() {
        super();
    }
}
