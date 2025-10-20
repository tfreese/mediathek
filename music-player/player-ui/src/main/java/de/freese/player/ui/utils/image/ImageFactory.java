// Created: 24 Aug. 2024
package de.freese.player.ui.utils.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

/**
 * @author Thomas Freese
 */
public final class ImageFactory {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    public static Icon getIcon(final String resource) {
        return getIcon(resource, 32, 32);
    }

    public static Icon getIcon(final String resource, final int width, final int height) {
        return new ImageIcon(getImage(resource, width, height));
    }

    public static synchronized Image getImage(final String resource, final int width, final int height) {
        final String key = "%s_%d_%d".formatted(resource, width, height);

        Image image = CACHE.get(key);

        if (image != null) {
            return image;
        }

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            if (resource.endsWith(".svg")) {
                image = loadSVG(resource, width, height, classLoader);
            }
            else {
                image = loadImage(resource, classLoader);
            }
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        catch (TranscoderException ex) {
            throw new RuntimeException(ex);
        }

        if (image != null) {
            CACHE.put(key, image);
        }
        else {
            throw new IllegalArgumentException("can not load image: " + resource);
        }

        return image;
    }

    private static BufferedImage loadImage(final String resource, final ClassLoader classLoader) throws IOException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            assert inputStream != null;

            return ImageIO.read(inputStream);
        }
    }

    private static BufferedImage loadSVG(final String resource, final int width, final int height, final ClassLoader classLoader) throws IOException, TranscoderException {
        try (InputStream inputStream = classLoader.getResourceAsStream(resource)) {
            assert inputStream != null;

            final BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) height);
            // transcoder.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);

            final TranscoderInput transcoderInput = new TranscoderInput(inputStream);
            transcoder.transcode(transcoderInput, null);

            return transcoder.getBufferedImage();
        }
    }

    private ImageFactory() {
        super();
    }
}
