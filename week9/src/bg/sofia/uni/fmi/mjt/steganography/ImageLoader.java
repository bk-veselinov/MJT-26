package bg.sofia.uni.fmi.mjt.steganography;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ImageLoader {

    private boolean isPng(Path file) throws IOException {
        return Files.probeContentType(file).equals("image/png");
    }

    public List<ImageFile> loadAllPngFromDir(String dirName) {
        List<ImageFile> images = new ArrayList<>();
        Path dir = Path.of(dirName);

        if (Files.isDirectory(dir)) {
            throw new IllegalArgumentException(String.format("%s is not a directory", dirName));
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {
                if (isPng(path)) {
                    images.add(new ImageFile(path.getFileName().toString(), loadImage(path)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Problem while reading files from %s", dirName), e);
        }
        images.sort(Comparator.comparing(ImageFile::name));
        return images;
    }

    public BufferedImage loadImage(Path imagePath) {
        try {
            return ImageIO.read(imagePath.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }

    public void saveImage(BufferedImage image, String outputDir, String name) {
        try {
            ImageIO.write(image, "png", new File(outputDir, name));
            System.out.println("Saved " + name + " to " + outputDir);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("While saving image %s", name), e);
        }
    }
}
