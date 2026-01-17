package bg.sofia.uni.fmi.mjt.newsfeed.http.parser;

import java.net.http.HttpResponse;

public interface JsonResponseParser<T> {
    T parseResponse(HttpResponse<String> response);
}
