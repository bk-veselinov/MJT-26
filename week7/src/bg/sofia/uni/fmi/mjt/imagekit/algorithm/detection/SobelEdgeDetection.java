package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private final ImageAlgorithm grayscaleAlgorithm;

    private static final int FULL_8_BIT_MASK = 0xFF;
    private static final int MAX_GRAY = 255;

    private static final int RED_OFFSET = 16;
    private static final int GREEN_OFFSET = 8;
    private static final int BLUE_OFFSET = 0;

    private static final int[] DIRECTIONS = {-1, 0, 1};
    private static final int[][] GX = {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
    };
    private static final int[][] GY = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        if (grayscaleAlgorithm == null) {
            throw new IllegalArgumentException("The grayscaleAlgorithm is null");
        }

        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    private int getPixelValue(BufferedImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return 0;
        }

        return image.getRGB(x, y) & FULL_8_BIT_MASK;
    }

    private int multiplyByKernal(BufferedImage image, int x, int y, int[][] kernal) {
        int change = 0;
        for (int xDirection : DIRECTIONS) {
            for (int yDirection : DIRECTIONS) {
                change += getPixelValue(image, x + xDirection, y + yDirection)
                        * kernal[1 + xDirection][1 + yDirection];
            }
        }
        return change;
    }

    private int multiplyByGx(BufferedImage image, int x, int y) {
        return multiplyByKernal(image, x, y, GX);
    }

    private int multiplyByGy(BufferedImage image, int x, int y) {
        return multiplyByKernal(image, x, y, GY);
    }

    private int getProcessedPixel(BufferedImage image, int x, int y) {
        int horizontalChange = multiplyByGx(image, x, y);
        int verticalChange = multiplyByGy(image, x, y);
        double gradientMagnitude = Math.sqrt(horizontalChange * horizontalChange + verticalChange * verticalChange);
        int pixelValue = Math.min(MAX_GRAY, (int) Math.round(gradientMagnitude));

        return (pixelValue << RED_OFFSET | pixelValue << GREEN_OFFSET | pixelValue << BLUE_OFFSET);

    }

    private void processPixels(BufferedImage oldImage, BufferedImage newImage) {
        for (int i = 0; i < oldImage.getWidth(); i++) {

            for (int j = 0; j < oldImage.getHeight(); j++) {

                int processedPixelValue = getProcessedPixel(oldImage, i, j);
                newImage.setRGB(i, j, processedPixelValue);
            }
        }
    }

    private BufferedImage processImage(BufferedImage oldImage) {
        int width = oldImage.getWidth();
        int height = oldImage.getHeight();

        BufferedImage newImage = grayscaleAlgorithm.process(
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));

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
