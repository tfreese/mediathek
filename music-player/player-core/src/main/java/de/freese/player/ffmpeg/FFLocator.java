// Created: 16 Juli 2024
package de.freese.player.ffmpeg;

import java.nio.file.Path;
import java.util.concurrent.Executor;

/**
 * @author Thomas Freese
 */
public final class FFLocator {
    public static FFmpeg createFFmpeg(final Executor executor, final Path tempDir) {
        return new DefaultFFmpeg("ffmpeg", executor, tempDir);
    }

    public static FFprobe createFFprobe() {
        return new DefaultFFprobe("ffprobe");
        // return new DefaultFFprobeRegEx("ffprobe");
    }

    private FFLocator() {
        super();
    }
}
