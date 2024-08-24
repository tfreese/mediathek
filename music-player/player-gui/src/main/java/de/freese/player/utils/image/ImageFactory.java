// Created: 24 Aug. 2024
package de.freese.player.utils.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;

/**
 * @author Thomas Freese
 */
public final class ImageFactory {
    private static final Map<String, Image> CACHE = new ConcurrentHashMap<>();

    public static Icon getIcon(final String resource) throws Exception {
        return getIcon(resource, 64, 64);
    }

    public static Icon getIcon(final String resource, final int width, final int height) throws Exception {
        return new ImageIcon(getImage(resource, width, height));
    }

    public static synchronized Image getImage(final String resource, final int width, final int height) throws Exception {
        final String key = "%s_%d_%d".formatted(resource, width, height);

        Image image = CACHE.get(key);

        if (image != null) {
            return image;
        }

        if (resource.endsWith(".svg")) {
            image = loadSVG(resource, width, height);
        }
        else {
            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
                assert inputStream != null;
                image = ImageIO.read(inputStream);
            }
        }

        if (image != null) {
            CACHE.put(key, image);
        }
        else {
            throw new IllegalArgumentException("can not load image: " + resource);
        }

        return image;
    }

    private static BufferedImage loadSVG(final String resource, final int width, final int height) throws Exception {
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            final BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) height);

            final TranscoderInput transcoderInput = new TranscoderInput(inputStream);
            transcoder.transcode(transcoderInput, null);

            return transcoder.getBufferedImage();
        }
    }

    private ImageFactory() {
        super();
    }
}
