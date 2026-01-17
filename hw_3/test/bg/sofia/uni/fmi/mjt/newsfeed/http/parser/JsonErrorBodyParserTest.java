package bg.sofia.uni.fmi.mjt.newsfeed.http.parser;

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
class JsonErrorBodyParserTest {

    private JsonErrorBodyParser parser;

    @Mock
    private HttpResponse<String> response;

    @BeforeEach
    void setUp() {
        parser = new JsonErrorBodyParser();
    }

    @Test
    void testParseResponseParsesError() {
        String jsonBody = """
                {
                    "status": "error",
                    "code": "apiKeyMissing",
                    "message": "message"
                }
                """;

        when(response.body()).thenReturn(jsonBody);

        ErrorBody result = parser.parseResponse(response);

        assertNotNull(result, "Result should not be null");
        assertEquals("error", result.status(), "Status should be error");
        assertEquals("apiKeyMissing", result.code(), "Code should match");
        assertEquals("message", result.message(), "Message should match");
    }

    @Test
    void testParseResponseThrowsOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parseResponse(null),
                "Should throw IllegalArgumentException when response is null");
    }
}