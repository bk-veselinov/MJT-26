package bg.sofia.uni.fmi.mjt.newsfeed.http.utility;

public record EndPointContext(String scheme, String host, String path, String... headers) {
}
