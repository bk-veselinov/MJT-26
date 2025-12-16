package bg.sofia.uni.fmi.mjt.steganography;

import java.awt.image.BufferedImage;
import java.util.Objects;

public record ImageFile(String name, BufferedImage data) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ImageFile imageFile = (ImageFile) o;
        return Objects.equals(name, imageFile.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
