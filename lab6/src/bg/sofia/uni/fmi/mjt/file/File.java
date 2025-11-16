package bg.sofia.uni.fmi.mjt.file;

import java.util.Objects;

/**
 * A simple in-memory representation of a file containing textual content.
 */
public class File {

    private String content;

    /**
     * Creates a new File with the given content.
     *
     * @param content the initial content of the file
     * @throws IllegalArgumentException if content is null
     */
    public File(String content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        File file = (File) o;
        return Objects.equals(content, file.content);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(content);
    }
}