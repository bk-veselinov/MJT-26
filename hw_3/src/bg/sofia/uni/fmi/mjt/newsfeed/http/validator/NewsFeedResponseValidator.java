package bg.sofia.uni.fmi.mjt.newsfeed.http.validator;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.ApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RateLimitedException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.SearchParametersException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonErrorBodyParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.ErrorBody;

import java.net.http.HttpResponse;

public class NewsFeedResponseValidator implements ResponseValidator {

    private static final int OK_CODE = 200;
    private static final int BAD_REQUEST_CODE = 400;
    private static final int UNAUTHORIZED_CODE = 401;
    private static final int TOO_MANY_REQUESTS_CODE = 429;
    private static final int SERVER_ERROR_CODE = 500;

    private final JsonResponseParser<ErrorBody> parser;

    public NewsFeedResponseValidator() {
        this.parser = new JsonErrorBodyParser();
    }

    NewsFeedResponseValidator(JsonErrorBodyParser parser) {
        this.parser = parser;
    }

    public void validate(HttpResponse<String> response) {
        int code = response.statusCode();

        if (code == OK_CODE) {
            return;
        }

        ErrorBody errorBody = parser.parseResponse(response);
        String errorMessage = errorBody.code() + ": " + errorBody.message();

        switch (code) {
            case BAD_REQUEST_CODE -> throw new SearchParametersException(errorMessage);
            case UNAUTHORIZED_CODE -> throw new ApiException(errorMessage);
            case TOO_MANY_REQUESTS_CODE -> throw new RateLimitedException(errorMessage);
            case SERVER_ERROR_CODE -> throw new ServerErrorException(errorMessage);
        }
    }
}
