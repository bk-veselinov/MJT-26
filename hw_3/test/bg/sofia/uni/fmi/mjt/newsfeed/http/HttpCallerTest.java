package bg.sofia.uni.fmi.mjt.newsfeed.http;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.entity.Source;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.EndPointContext;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;
import bg.sofia.uni.fmi.mjt.newsfeed.http.validator.ResponseValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class HttpCallerTest {


    private final EndPointContext endPointContext =
            new EndPointContext("https", "newsapi.org", "/v2/top-headlines", "x-api-key", "api key");
    @Mock
    private ResponseValidator validator;
    @Mock
    private HttpClient client;
    @Mock
    private HttpResponse<String> response;
    @Mock
    private JsonResponseParser<List<Article>> parser;

    private HttpCaller caller;

    @BeforeEach
    void setUp() {
        caller = new HttpCaller(endPointContext, validator, client);
    }

    @Test
    void testGetResponseAsJsonReturnsBodyOnSuccess() throws IOException, InterruptedException {
        String expectedJson = "{\"status\": \"ok\", \"totalResults\": 0, \"articles\": []}";

        when(response.body()).thenReturn(expectedJson);
        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        Request request = mock();
        String result = caller.getResponseAsJson(request);

        assertEquals(expectedJson, result, "The returned JSON should match the response body.");

        verify(validator).validate(response);
        verify(client).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void testGetParsedObjectParsesResponse() throws IOException, InterruptedException {
        List<Source> expectedSources = List.of(
                new Source("id1", "name1"),
                new Source("id2", "name2"),
                new Source("id3", "name3")
        );
        List<Article> expectedArticles = List.of(
                new Article(expectedSources.get(0), "Author1", "Title1", "Desc1", URI.create("http://url1.com"), null, "2026-01-18T10:00:00Z", "Content1"),
                new Article(expectedSources.get(1), "Author2", "Title2", "Desc2", URI.create("http://url2.com"), null, "2026-01-18T12:00:00Z", "Content2"),
                new Article(expectedSources.get(2), "Author1", "Title3", "Desc3", URI.create("http://url3.com"), null, "2026-01-18T14:00:00Z", "Content3")
        );

        when(client.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);
        when(parser.parseResponse(response)).thenReturn(expectedArticles);

        Request request = mock();
        List<Article> result = caller.getParsedObject(request, parser);

        assertEquals(expectedArticles, result, "The returned object should match the parsed result.");
        verify(validator).validate(response);
    }

}