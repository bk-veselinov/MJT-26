package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    private static boolean isValidFormat(String imageName) {
        Pattern pattern = Pattern.compile("\\.(jpeg|jpg|png|bmp)$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(imageName);
        return matcher.find();
    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException("Image file is null");
        }

        if (!imageFile.exists()) {
            throw new IOException("The image file does not exist");
        }

        if (!imageFile.isFile()) {
            throw new IOException("Image file is not a file");
        }

        if (!isValidFormat(imageFile.getName().toLowerCase())) {
            throw new IOException(String.format("Image %s is not in the allowed formats (.jpeg, .png, .bmp)",
                    imageFile.getName().toLowerCase()));
        }

        return ImageIO.read(imageFile);
    }

    private List<BufferedImage> getImagesFromDir(File imagesDirectory) throws IOException {
        List<BufferedImage> images = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDirectory.toPath())) {
            for (Path img : stream) {
                images.add(loadImage(img.toFile()));
            }

        } catch (IOException e) {
            throw new IOException("Problem while reading images in directory", e);
        }

        return images;
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if (imagesDirectory == null) {
            throw new IllegalArgumentException("Directory is null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()) {
            throw new IOException("the imagesDirectory does not exist or it is not directory");
        }

        return getImagesFromDir(imagesDirectory);
    }

    private static String getExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');

        return fileName.substring(dotIndex + 1);

    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null) {
            throw new IllegalArgumentException("Buffered image is null");
        }

        if (imageFile == null) {
            throw new IllegalArgumentException("Image file is null");
        }

        if (imageFile.exists()) {
            throw new IOException("Image file with this name already exist");
        }

        File parentDir = imageFile.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            throw new IOException("Parent directory does not exist");
        }

        ImageIO.write(image, getExtension(imageFile), imageFile);
    }
}
