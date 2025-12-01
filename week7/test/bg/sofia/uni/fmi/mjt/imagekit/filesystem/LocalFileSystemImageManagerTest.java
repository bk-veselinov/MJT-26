package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalFileSystemImageManagerTest {

    private LocalFileSystemImageManager imageManager;

    @Mock
    private File mockFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        imageManager = new LocalFileSystemImageManager();
    }

    @Test
    void testLoadImageThrowsArgumentsExceptionIfFileIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> imageManager.loadImage(null),
                "loadImage should throw IllegalArgumentException when file is null");
    }

    @Test
    void testLoadImageThrowsIOExceptionIfFileDoesNotExist() {
        when(mockFile.exists()).thenReturn(false);

        assertThrows(IOException.class,
                () -> imageManager.loadImage(mockFile),
                "loadImage should throw IOException when file does not exist");
    }

    @Test
    void testLoadImageThrowsIOExceptionIfIsNotAFile() {
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(false);

        assertThrows(IOException.class,
                () -> imageManager.loadImage(mockFile),
                "loadImage should throw IOException when path is not a file");
    }

    @Test
    void testLoadImageThrowsIOExceptionIfFileFormatIsInvalid() {
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isFile()).thenReturn(true);
        when(mockFile.getName()).thenReturn("image.txt");

        assertThrows(IOException.class,
                () -> imageManager.loadImage(mockFile),
                "loadImage should throw IOException for unsupported file extensions");
    }

    @Test
    void testLoadImageSuccessWithRealFile() throws IOException {
        File validImage = createDummyImage(tempDir.resolve("test.png").toFile(), "png");

        BufferedImage result = imageManager.loadImage(validImage);

        assertNotNull(result, "Loaded BufferedImage should not be null");
        assertEquals(10, result.getWidth(), "Loaded image width should match saved image");
        assertEquals(10, result.getHeight(), "Loaded image height should match saved image");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsIfDirectoryIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> imageManager.loadImagesFromDirectory(null),
                "Should throw IllegalArgumentException if directory is null");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsIfDirectoryDoesNotExist() {
        when(mockFile.exists()).thenReturn(false);

        assertThrows(IOException.class,
                () -> imageManager.loadImagesFromDirectory(mockFile),
                "Should throw IOException if directory does not exist");
    }

    @Test
    void testLoadImagesFromDirectoryThrowsIfIsNotDirectory() {
        when(mockFile.exists()).thenReturn(true);
        when(mockFile.isDirectory()).thenReturn(false);

        assertThrows(IOException.class,
                () -> imageManager.loadImagesFromDirectory(mockFile),
                "Should throw IOException if path is not a directory");
    }

    @Test
    void testLoadImagesFromDirectorySuccess() throws IOException {
        createDummyImage(tempDir.resolve("img1.png").toFile(), "png");
        createDummyImage(tempDir.resolve("img2.jpg").toFile(), "jpg");

        List<BufferedImage> images = imageManager.loadImagesFromDirectory(tempDir.toFile());

        assertEquals(2, images.size(), "Should load exactly 2 images from the directory");
    }

    @Test
    void testLoadImagesFromDirectoryFailsOnInvalidFileInDir() throws IOException {
        createDummyImage(tempDir.resolve("img1.png").toFile(), "png");

        File invalidFile = tempDir.resolve("notes.txt").toFile();
        invalidFile.createNewFile();

        assertThrows(IOException.class,
                () -> imageManager.loadImagesFromDirectory(tempDir.toFile()),
                "Should throw IOException because directory contains non-image files");
    }

    @Test
    void testSaveImageThrowsIfImageIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> imageManager.saveImage(null, mockFile),
                "Should throw IllegalArgumentException if image is null");
    }

    @Test
    void testSaveImageThrowsIfFileIsNull() {
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        assertThrows(IllegalArgumentException.class,
                () -> imageManager.saveImage(dummyImg, null),
                "Should throw IllegalArgumentException if file is null");
    }

    @Test
    void testSaveImageThrowsIfFileAlreadyExists() {
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        when(mockFile.exists()).thenReturn(true);

        assertThrows(IOException.class,
                () -> imageManager.saveImage(dummyImg, mockFile),
                "Should throw IOException if output file already exists");
    }

    @Test
    void testSaveImageThrowsIfParentDirectoryDoesNotExist() {
        BufferedImage dummyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        File parentDir = mock(File.class);

        when(mockFile.exists()).thenReturn(false);
        when(mockFile.getParentFile()).thenReturn(parentDir);
        when(parentDir.exists()).thenReturn(false); // Parent dir missing

        assertThrows(IOException.class,
                () -> imageManager.saveImage(dummyImg, mockFile),
                "Should throw IOException if parent directory does not exist");
    }

    @Test
    void testSaveImageSuccess() {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        File outputFile = tempDir.resolve("output.png").toFile();

        assertDoesNotThrow(() -> imageManager.saveImage(image, outputFile));

        assertTrue(outputFile.exists(), "Image file should be created");
        assertTrue(outputFile.length() > 0, "Image file should not be empty");
    }

    private File createDummyImage(File file, String format) throws IOException {
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, format, file);
        return file;
    }
}