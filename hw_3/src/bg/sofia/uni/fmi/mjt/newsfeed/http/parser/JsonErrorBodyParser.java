package bg.sofia.uni.fmi.mjt.newsfeed.http.parser;

import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.ErrorBody;
import com.google.gson.Gson;

import java.net.http.HttpResponse;

public class JsonErrorBodyParser implements JsonResponseParser<ErrorBody> {

    private static final Gson GSON = new Gson();

    @Override
    public ErrorBody parseResponse(HttpResponse<String> response) {
        if (response == null) {
            throw new IllegalArgumentException("Response cannot be null");
        }

        return GSON.fromJson(response.body(), ErrorBody.class);
    }
}
