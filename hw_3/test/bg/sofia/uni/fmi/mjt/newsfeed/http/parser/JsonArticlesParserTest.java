package bg.sofia.uni.fmi.mjt.newsfeed.http.parser;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonArticlesParserTest {

    private JsonArticlesParser parser;

    @Mock
    private HttpResponse<String> response;

    @BeforeEach
    void setUp() {
        parser = new JsonArticlesParser();
    }

    @Test
    void testParseResponseParsesArticles() {
        String jsonBody = """
                {
                "status": "ok",
                "totalResults": 2,
                "articles": [
                    {
                        "source": {
                            "id": "id1",
                            "name": "name 1"
                        },
                        "author": "Author1",
                        "title": "Title1",
                        "description": "Desc1",
                        "url": "http://url1.com",
                        "urlToImage": "http://urlImg1.com",
                        "publishedAt": "2026-01-01T10:00:00Z",
                        "content": "Content1"
                    },
                    {
                        "source": {
                            "id": "id2",
                            "name": "name 2"
                        },
                        "author": "Author2",
                        "title": "Title2",
                        "description": "Desc2",
                        "url": "http://url2.com",
                        "urlToImage": "http://urlImg2.com",
                        "publishedAt": "2026-01-01T10:00:00Z",
                        "content": "Content2"
                    }
                ]
                }
                """;

        when(response.body()).thenReturn(jsonBody);

        List<Article> result = parser.parseResponse(response);

        assertEquals(2, result.size(), "Should parse 2 articles");
        assertEquals("Author1", result.get(0).author(), "First article author should match");
        assertEquals(URI.create("http://url1.com"), result.get(0).url(), "First article URL should match");
        assertEquals("Title2", result.get(1).title(), "Second article title should match");
    }

    @Test
    void testParseResponseThrowsOnNull() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parseResponse(null),
                "Should throw IllegalArgumentException when response is null");
    }
}