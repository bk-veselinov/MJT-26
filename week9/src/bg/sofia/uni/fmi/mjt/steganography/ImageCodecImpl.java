package bg.sofia.uni.fmi.mjt.steganography;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageCodecImpl implements ImageCodec {

    private static final int RED_OFFSET = 16;
    private static final int GREEN_OFFSET = 8;
    private static final int BLUE_OFFSET = 0;
    private static final int CHANNELS_COUNT = 3;
    private static final int[] CHANNELS_OFFSETS = {BLUE_OFFSET, GREEN_OFFSET, RED_OFFSET};
    private static final int FULL_BYTE_MASK = 0xff;
    private static final int THREE_BIT_MASK = 0b11;
    private static final int BIT_MASK = 0b1;

    private static final int PIXEL_TO_EMBED_WEIGHT_COUNT = 4;
    private static final int PIXEL_TO_EMBED_HEIGHT_COUNT = 4;

    private final ImageLoader loader = new ImageLoader();

    private int getChanelData(int pixelData, int chanelOffset) {
        return (pixelData >> chanelOffset) & FULL_BYTE_MASK;
    }

    private int embedBitInChanel(int bit, int pixel, int chanelOffset) {
        return getChanelData(pixel, chanelOffset) | bit;
    }

    private void embedDataInPixel(int data, int x, int y, BufferedImage image) {
        int pixel = image.getRGB(x, y);
        int processedPixel = 0;

        for (int i = 0; i < CHANNELS_COUNT; i++) {
            int newChanelData = embedBitInChanel((data >> i) & BIT_MASK, pixel, CHANNELS_OFFSETS[i]);
            processedPixel = processedPixel | (newChanelData << CHANNELS_OFFSETS[i]);
        }

        image.setRGB(x, y, processedPixel);
    }

    private void embedWidth(int width, BufferedImage image) {
        for (int i = 0; i < PIXEL_TO_EMBED_WEIGHT_COUNT; i++) {
            int data = (width >> (i * THREE_BIT_MASK)) & THREE_BIT_MASK;

            embedDataInPixel(data, 0, PIXEL_TO_EMBED_WEIGHT_COUNT - i, image);
        }
    }

    private void embedHeight(int height, BufferedImage image) {
        for (int i = 0; i < PIXEL_TO_EMBED_HEIGHT_COUNT; i++) {
            int data = (height >> (i * THREE_BIT_MASK)) & THREE_BIT_MASK;

            embedDataInPixel(data, 0,
                    PIXEL_TO_EMBED_WEIGHT_COUNT + PIXEL_TO_EMBED_HEIGHT_COUNT - i, image);
        }
    }

    private BufferedImage getEmbeddedImage(BufferedImage cover, BufferedImage secret) {
        embedWidth(secret.getWidth(), cover);
        embedHeight(secret.getHeight(), cover);
    }

    @Override
    public void embedPNGImages(String coverSourceDirectory, String secretSourceDirectory, String outputDirectory) {
        List<ImageFile> coverImages = loader.loadAllPngFromDir(coverSourceDirectory);
        List<ImageFile> secretImages = loader.loadAllPngFromDir(secretSourceDirectory);

        for (int i = 0; i < coverImages.size(); i++) {
            ImageFile coverImage = coverImages.get(i);
            ImageFile secretImage = secretImages.get(i);
            loader.saveImage(
                    getEmbeddedImage(coverImage.data(), secretImage.data()),
                    outputDirectory,
                    coverImage.name()
            );
        }
    }

    private BufferedImage extractImage(BufferedImage image) {

    }

    @Override
    public void extractPNGImages(String sourceDirectory, String outputDirectory) {

    }
}
