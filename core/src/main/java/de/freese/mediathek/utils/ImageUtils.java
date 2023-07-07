package de.freese.mediathek.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author Thomas Freese
 */
public final class ImageUtils {
    public static RenderingHints getRenderingHintsQuality() {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return hints;
    }

    public static BufferedImage scaleImage(final Image src, final int width, final int height) {
        Image scaled = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return toBufferedImage(scaled);
    }

    public static BufferedImage scaleImageByRatio(final Image src, final double ratioWidth, final double ratioHeight) {
        BufferedImage bufferedImage = toBufferedImage(src);

        AffineTransform tx = new AffineTransform();
        tx.scale(ratioWidth, ratioHeight);

        // tx.shear(shiftx, shifty);
        // tx.translate(x, y);
        // tx.rotate(radians, origin.getWidth()/2, origin.getHeight()/2);

        RenderingHints hints = getRenderingHintsQuality();

        AffineTransformOp op = new AffineTransformOp(tx, hints);

        return op.filter(bufferedImage, null);
    }

    public static BufferedImage scaleImageKeepRatio(final Image src, final int maxWidth, final int maxHeight) {
        BufferedImage bufferedImage = toBufferedImage(src);

        double widthRatio = (double) maxWidth / bufferedImage.getWidth();
        double heightRatio = (double) maxHeight / bufferedImage.getHeight();

        double ratio = Math.min(widthRatio, heightRatio);

        return scaleImageByRatio(bufferedImage, ratio, ratio);

        // double newWidth = bufferedImage.getWidth() * ratio;
        // double newHeight = bufferedImage.getHeight() * ratio;
        //
        // return scaleImage(bufferedImage, (int) newWidth, (int) newHeight);
    }

    public static BufferedImage toBufferedImage(final Icon icon) {
        if (icon instanceof ImageIcon imageIcon) {
            if (imageIcon.getImage() instanceof BufferedImage bi) {
                return bi;
            }
        }

        BufferedImage returnImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = returnImage.getGraphics();

        icon.paintIcon(null, graphics, 0, 0);
        graphics.dispose();

        return returnImage;
    }

    public static BufferedImage toBufferedImage(final Image image) {
        if (image instanceof BufferedImage bi) {
            return bi;
        }

        BufferedImage bufferedImage;

        // boolean hasAlpha = hasAlpha(image);
        //
        // try
        // {
        // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        //
        // int transparency = Transparency.OPAQUE;
        //
        // if (hasAlpha)
        // {
        // transparency = Transparency.BITMASK;
        // }
        //
        // GraphicsDevice gs = ge.getDefaultScreenDevice();
        // GraphicsConfiguration gc = gs.getDefaultConfiguration();
        //
        // bufferedImage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        // }
        // catch (HeadlessException ex)
        // {
        // // Keine GUI vorhanden
        // }
        //
        // if (bufferedImage == null)
        // {
        // int type = BufferedImage.TYPE_INT_RGB;
        //
        // if (hasAlpha)
        // {
        // type = BufferedImage.TYPE_INT_ARGB;
        // }
        //
        // bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        // }

        bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        RenderingHints hints = getRenderingHintsQuality();

        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setRenderingHints(hints);

        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();

        return bufferedImage;
    }

    private ImageUtils() {
        super();
    }
}
