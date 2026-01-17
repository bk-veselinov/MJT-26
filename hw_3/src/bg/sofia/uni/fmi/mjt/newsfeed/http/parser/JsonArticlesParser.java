package bg.sofia.uni.fmi.mjt.newsfeed.http.parser;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.HeadlinesBody;
import com.google.gson.Gson;

import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class JsonArticlesParser implements JsonResponseParser<List<Article>> {

    private static final Gson GSON = new Gson();

    @Override
    public List<Article> parseResponse(HttpResponse<String> response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }

        return Arrays.stream(GSON.fromJson(response.body(), HeadlinesBody.class).articles()).toList();
    }
}
