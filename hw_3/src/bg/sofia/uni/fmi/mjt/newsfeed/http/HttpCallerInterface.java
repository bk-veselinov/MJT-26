package bg.sofia.uni.fmi.mjt.newsfeed.http;

import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;

public interface HttpCallerInterface {

    String getResponseAsJson(Request request);

    <T, R extends Request> T getParsedObject(R newsFeedRequest, JsonResponseParser<T> parser);
}
