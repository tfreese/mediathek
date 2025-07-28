// Created: 29.11.2018
package de.freese.player.ui.utils.image;

import java.awt.image.BufferedImage;

import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

/**
 * @author Thomas Freese
 */
final class BufferedImageTranscoder extends ImageTranscoder {
    private BufferedImage image;

    @Override
    public BufferedImage createImage(final int width, final int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void writeImage(final BufferedImage img, final TranscoderOutput output) {
        this.image = img;
    }

    BufferedImage getBufferedImage() {
        return image;
    }
}
