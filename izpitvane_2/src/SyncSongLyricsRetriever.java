import com.google.gson.Gson;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

public class SyncSongLyricsRetriever {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String SCHEME = "https";
    private static final String HOST = "newsapi.org";
    private static final String PATH = "/v2/top-headlines";

    public String getLyricsSync() throws Exception {
        String query = "country=us&q=trump&q=a&";
        URI uri = new URI(SCHEME, HOST, PATH, query, null);

        System.out.println(uri);
        HttpRequest request = HttpRequest.newBuilder(uri).header("x-api-key", "4fcb365f1527413f81ea8a102161649c").build();

        String json = HTTP_CLIENT.send(request, BodyHandlers.ofString()).body();

        return json;
    }

    static void main() throws Exception {
        System.out.println(
                new SyncSongLyricsRetriever().getLyricsSync()
        );

    }
}