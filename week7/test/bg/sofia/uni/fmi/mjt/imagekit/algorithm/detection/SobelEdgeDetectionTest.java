package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SobelEdgeDetectionTest {

    @Mock
    private ImageAlgorithm mockGrayscaleAlgorithm;

    private SobelEdgeDetection sobelAlgorithm;

    @BeforeEach
    void setUp() {
        sobelAlgorithm = new SobelEdgeDetection(mockGrayscaleAlgorithm);
    }

    @Test
    void testConstructorThrowsIfGrayscaleAlgorithmIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new SobelEdgeDetection(null),
                "Constructor should throw IllegalArgumentException if dependency is null");
    }

    @Test
    void testProcessThrowsIfImageIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> sobelAlgorithm.process(null),
                "Process should throw IllegalArgumentException if image is null");
    }

    @Test
    void testProcessMaintainsDimensions() {
        int width = 10;
        int height = 10;
        BufferedImage input = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        when(mockGrayscaleAlgorithm.process(any(BufferedImage.class)))
                .thenAnswer(invocation -> {
                    return invocation.getArgument(0);
                });

        BufferedImage result = sobelAlgorithm.process(input);

        assertNotNull(result);
        assertEquals(width, result.getWidth());
        assertEquals(height, result.getHeight());
    }

    @Test
    void testProcessUniformColorShouldResultInBlack() {
        int width = 5;
        int height = 5;
        BufferedImage input = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                input.setRGB(x, y, Color.WHITE.getRGB());
            }
        }

        when(mockGrayscaleAlgorithm.process(any(BufferedImage.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BufferedImage result = sobelAlgorithm.process(input);

        int centerPixel = result.getRGB(2, 2) & 0xFFFFFF; // Mask alpha
        assertEquals(0, centerPixel, "Uniform area should produce 0 gradient (Black)");
    }

    @Test
    void testProcessVerticalEdge() {
        BufferedImage input = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();

        // Set Column 0 to Black
        for (int y = 0; y < 3; y++) input.setRGB(0, y, black);
        // Set Columns 1 & 2 to White
        for (int y = 0; y < 3; y++) input.setRGB(1, y, white);
        for (int y = 0; y < 3; y++) input.setRGB(2, y, white);

        when(mockGrayscaleAlgorithm.process(any(BufferedImage.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BufferedImage result = sobelAlgorithm.process(input);

        int centerPixel = result.getRGB(1, 1) & 0xFF;

        assertEquals(255, centerPixel, "Vertical edge should be detected as max brightness");
    }

    @Test
    void testProcessHorizontalEdge() {
        BufferedImage input = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);

        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();

        // Set Row 0 to Black
        for (int x = 0; x < 3; x++) input.setRGB(x, 0, black);
        // Set Rows 1 & 2 to White
        for (int x = 0; x < 3; x++) input.setRGB(x, 1, white);
        for (int x = 0; x < 3; x++) input.setRGB(x, 2, white);

        when(mockGrayscaleAlgorithm.process(any(BufferedImage.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        BufferedImage result = sobelAlgorithm.process(input);

        int centerPixel = result.getRGB(1, 1) & 0xFF;

        assertEquals(255, centerPixel, "Horizontal edge should be detected as max brightness");
    }
}