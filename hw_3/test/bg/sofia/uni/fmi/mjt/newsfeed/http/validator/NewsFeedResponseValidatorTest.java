package bg.sofia.uni.fmi.mjt.newsfeed.http.validator;

import bg.sofia.uni.fmi.mjt.newsfeed.exception.ApiException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.RateLimitedException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.SearchParametersException;
import bg.sofia.uni.fmi.mjt.newsfeed.exception.ServerErrorException;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonErrorBodyParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.ErrorBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedResponseValidatorTest {

    private NewsFeedResponseValidator validator;

    @Mock
    private JsonErrorBodyParser parser;
    @Mock
    private HttpResponse<String> response;

    @BeforeEach
    void setUp() {
        validator = new NewsFeedResponseValidator(parser);
    }

    @Test
    void testValidateDoesNothingOnOk() {
        when(response.statusCode()).thenReturn(200);

        assertDoesNotThrow(() -> validator.validate(response), "Should not throw exception on 200 OK");
    }

    @Test
    void testValidateThrowsOnBadRequest() {
        when(response.statusCode()).thenReturn(400);
        when(parser.parseResponse(response)).thenReturn(new ErrorBody("error", "code400", "Bad Request"));
        assertThrows(SearchParametersException.class,
                () -> validator.validate(response),
                "Should throw SearchParametersException on 400");
    }

    @Test
    void testValidateThrowsOnUnauthorized() {
        when(response.statusCode()).thenReturn(401);
        when(parser.parseResponse(response)).thenReturn(new ErrorBody("error", "code401", "Unauthorized"));

        assertThrows(ApiException.class,
                () -> validator.validate(response),
                "Should throw ApiException on 401");
    }

    @Test
    void testValidateThrowsOnTooManyRequests() {
        when(response.statusCode()).thenReturn(429);
        when(parser.parseResponse(response)).thenReturn(new ErrorBody("error", "code429", "Too Many Requests"));

        assertThrows(RateLimitedException.class,
                () -> validator.validate(response),
                "Should throw RateLimitedException on 429");
    }

    @Test
    void testValidateThrowsOnServerError() {
        when(response.statusCode()).thenReturn(500);
        when(parser.parseResponse(response)).thenReturn(new ErrorBody("error", "code500", "Server Error"));
        assertThrows(ServerErrorException.class,
                () -> validator.validate(response),
                "Should throw ServerErrorException on 500");
    }
}