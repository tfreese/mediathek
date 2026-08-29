package de.freese.player.core.ffmpeg;

/**
 * @author Thomas Freese
 * @since 16.07.2024
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
