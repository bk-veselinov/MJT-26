package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LuminosityGrayscaleTest {

    private LuminosityGrayscale grayscaleAlgorithm;

    @BeforeEach
    void setUp() {
        grayscaleAlgorithm = new LuminosityGrayscale();
    }

    @Test
    void testProcessThrowsExceptionIfImageIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> grayscaleAlgorithm.process(null),
                "Process should throw IllegalArgumentException when input image is null");
    }

    @Test
    void testProcessMaintainsImageDimensions() {
        BufferedImage original = new BufferedImage(10, 20, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = grayscaleAlgorithm.process(original);

        assertNotNull(result, "Resulting image should not be null");
        assertEquals(original.getWidth(), result.getWidth(), "Width should be preserved");
        assertEquals(original.getHeight(), result.getHeight(), "Height should be preserved");
    }

    @Test
    void testProcessPureRedPixel() {
        testSinglePixelConversion(new Color(255, 0, 0), 54);
    }

    @Test
    void testProcessPureGreenPixel() {
        testSinglePixelConversion(new Color(0, 255, 0), 184);
    }

    @Test
    void testProcessPureBluePixel() {
        testSinglePixelConversion(new Color(0, 0, 255), 18);
    }

    @Test
    void testProcessMixedColorPixel() {
        testSinglePixelConversion(new Color(100, 150, 200), 143);
    }

    @Test
    void testProcessBlackAndWhite() {
        testSinglePixelConversion(new Color(0, 0, 0), 0);
        testSinglePixelConversion(new Color(255, 255, 255), 255);
    }

    private void testSinglePixelConversion(Color inputColor, int expectedGray) {
        BufferedImage original = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        original.setRGB(0, 0, inputColor.getRGB());

        BufferedImage result = grayscaleAlgorithm.process(original);
        int resultPixel = result.getRGB(0, 0);

        int resultRed = (resultPixel >> 16) & 0xFF;
        int resultGreen = (resultPixel >> 8) & 0xFF;
        int resultBlue = resultPixel & 0xFF;

        assertEquals(expectedGray, resultRed, "Red component calculation is incorrect");
        assertEquals(expectedGray, resultGreen, "Green component calculation is incorrect");
        assertEquals(expectedGray, resultBlue, "Blue component calculation is incorrect");
    }
}