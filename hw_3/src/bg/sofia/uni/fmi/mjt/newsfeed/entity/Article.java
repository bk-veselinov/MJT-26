package bg.sofia.uni.fmi.mjt.newsfeed.entity;

import java.net.URI;
import java.util.Objects;

public record Article(
        Source source,
        String author,
        String title,
        String description,
        URI url,
        URI urlToImage,
        String publishedAt,
        String content) {

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(url, article.url);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url);
    }
}
