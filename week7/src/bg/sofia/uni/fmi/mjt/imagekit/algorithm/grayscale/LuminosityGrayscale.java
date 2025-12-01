package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final double RED_WEIGHT = 0.21;
    private static final double GREEN_WEIGHT = 0.72;
    private static final double BLUE_WEIGHT = 0.07;

    private static final int RED_OFFSET = 16;
    private static final int GREEN_OFFSET = 8;
    private static final int BLUE_OFFSET = 0;
    private static final int FULL_8_BIT_MASK = 0xFF;

    private static final int MAX_GRAY = 255;

    private static int getProcessedPixel(BufferedImage image, int x, int y) {
        int pixel = image.getRGB(x, y);

        int red = (pixel >> RED_OFFSET) & FULL_8_BIT_MASK;
        int green = (pixel >> GREEN_OFFSET) & FULL_8_BIT_MASK;
        int blue = (pixel >> BLUE_OFFSET) & FULL_8_BIT_MASK;

        double newPixelLuminosity = RED_WEIGHT * red + GREEN_WEIGHT * green + BLUE_WEIGHT * blue;
        int luminosity = Math.min(MAX_GRAY, (int) Math.round(newPixelLuminosity));

        return (luminosity << RED_OFFSET | luminosity << GREEN_OFFSET | luminosity << BLUE_OFFSET);
    }

    private static void processPixels(BufferedImage oldImage, BufferedImage newImage) {
        for (int i = 0; i < oldImage.getWidth(); i++) {

            for (int j = 0; j < oldImage.getHeight(); j++) {

                int processedPixelValue = getProcessedPixel(oldImage, i, j);
                newImage.setRGB(i, j, processedPixelValue);
            }
        }
    }

    private static BufferedImage processImage(BufferedImage oldImage) {
        int width = oldImage.getWidth();
        int height = oldImage.getHeight();

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        processPixels(oldImage, newImage);

        return newImage;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image is null");
        }

        return processImage(image);
    }
}
