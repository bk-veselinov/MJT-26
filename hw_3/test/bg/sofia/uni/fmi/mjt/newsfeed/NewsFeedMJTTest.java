package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.entity.Source;
import bg.sofia.uni.fmi.mjt.newsfeed.http.HttpCallerInterface;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedMJTTest {

    @Mock
    private HttpCallerInterface caller;
    @Mock
    private JsonResponseParser<List<Article>> parser;

    private NewsFeedMJT newsFeed;
    private List<Source> sampleSource;
    private List<Article> sampleArticles;

    @BeforeEach
    void setUp() {
        newsFeed = new NewsFeedMJT(caller, parser);
        sampleSource = List.of(
                new Source("id1", "name1"),
                new Source("id2", "name2"),
                new Source("id3", "name3")
        );
        sampleArticles = List.of(
                new Article(sampleSource.get(0), "Author1", "Title1", "Desc1", URI.create("http://url1.com"), URI.create("http://urlImg1.com"), "2026-01-18T10:00:00Z", "Content1"),
                new Article(sampleSource.get(1), "Author2", "Title2", "Desc2", URI.create("http://url2.com"), URI.create("http://urlImg2.com"), "2026-01-18T12:00:00Z", "Content2"),
                new Article(sampleSource.get(2), "Author1", "Title3", "Desc3", URI.create("http://url3.com"), URI.create("http://urlImg3.com"), "2026-01-18T14:00:00Z", "Content3")
        );
    }

    @Test
    void testGetNewsByRequestUsesCaller() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        Request request = mock();
        List<Article> result = newsFeed.getNewsByRequest(request);

        assertEquals(sampleArticles, result, "The returned articles should match the mocked");
        verify(caller).getParsedObject(request, parser);
    }

    @Test
    void testGetNewsByKeyWordsWorksCorrectly() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        List<Article> result = newsFeed.getNewsByKeyWords("keyword1", "keyword2");

        assertEquals(sampleArticles, result, "The returned articles should match the mocked");
    }

    @Test
    void testGetNewsByAuthorGroupsArticlesByAuthor() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        Request request = mock();
        Map<String, List<Article>> result = newsFeed.getNewsByAuthor(request);

        assertEquals(2, result.size(), "Should have 2 authors.");
        assertTrue(result.containsKey("Author1"), "Should contain Author1");
        assertTrue(result.containsKey("Author2"), "Should contain Author2");
        assertEquals(2, result.get("Author1").size(), "Author1 should have 2 articles");
        assertEquals(1, result.get("Author2").size(), "Author2 should have 1 article");
    }

    @Test
    void testGetNewsByAuthorWithKeywordsGroupsArticlesByAuthor() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        Map<String, List<Article>> result = newsFeed.getNewsByAuthor("keyword");

        assertEquals(2, result.size(), "Should have 2 authors.");
        assertTrue(result.containsKey("Author1"), "Should contain Author1");
        assertTrue(result.containsKey("Author2"), "Should contain Author2");
    }

    @Test
    void testGetNewsBeforeFiltersArticlesByDate() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime cutoff = LocalDateTime.parse("2026-01-18T13:00:00");
        Request request = mock();

        List<Article> result = newsFeed.getNewsBefore(cutoff, request);

        assertEquals(2, result.size(), "Should return 2 articles before 13:00");
    }

    @Test
    void testGetNewsBeforeWithKeywordsFiltersArticlesByDate() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime cutoff = LocalDateTime.parse("2026-01-18T13:00:00");

        List<Article> result = newsFeed.getNewsBefore(cutoff, "keyword");

        assertEquals(2, result.size(), "Should return 2 articles before 13:00");
    }

    @Test
    void testGetNewsAfterFiltersArticlesByDate() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime cutoff = LocalDateTime.parse("2026-01-18T11:00:00");
        Request request = mock();

        List<Article> result = newsFeed.getNewsAfter(cutoff, request);

        assertEquals(2, result.size(), "Should return 2 articles after 11:00");
    }

    @Test
    void testGetNewsAfterWithKeywordsFiltersArticlesByDate() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime cutoff = LocalDateTime.parse("2026-01-18T11:00:00");

        List<Article> result = newsFeed.getNewsAfter(cutoff, "keyword");

        assertEquals(2, result.size(), "Should return 2 articles after 11:00");
    }

    @Test
    void testGetNewsBetweenFiltersArticlesByDateRange() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime from = LocalDateTime.parse("2026-01-18T11:00:00");
        LocalDateTime to = LocalDateTime.parse("2026-01-18T13:00:00");
        Request request = mock();

        List<Article> result = newsFeed.getNewsBetween(from, to, request);

        assertEquals(1, result.size(), "Should return 1 article between 11:00 and 13:00");
        assertEquals("Title2", result.get(0).title(), "Should be the second article");
    }

    @Test
    void testGetNewsBetweenThrowsOnNullFrom() {
        Request request = mock();

        assertThrows(IllegalArgumentException.class,
                () -> newsFeed.getNewsBetween(null, LocalDateTime.parse("2026-01-10T10:00:00"), request),
                "Should throw IllegalArgumentException when from is null");
    }

    @Test
    void testGetNewsBetweenThrowsOnNullTo() {
        Request request = mock();

        assertThrows(IllegalArgumentException.class,
                () -> newsFeed.getNewsBetween(LocalDateTime.parse("2026-01-10T10:00:00"), null, request),
                "Should throw IllegalArgumentException when to is null");
    }

    @Test
    void testGetNewsBetweenThrowsOnInvalidTimeFrame() {
        Request request = mock();

        assertThrows(IllegalArgumentException.class,
                () -> newsFeed.getNewsBetween(
                        LocalDateTime.parse("2026-01-11T10:00:00"),
                        LocalDateTime.parse("2026-01-10T10:00:00"),
                        request),
                "Should throw IllegalArgumentException to is before from");
    }

    @Test
    void testGetNewsBetweenWithKeywordsFiltersArticlesByDateRange() {
        when(caller.getParsedObject(any(), any())).thenReturn(sampleArticles);

        LocalDateTime from = LocalDateTime.parse("2026-01-18T11:00:00");
        LocalDateTime to = LocalDateTime.parse("2026-01-18T13:00:00");

        List<Article> result = newsFeed.getNewsBetween(from, to, "keyword");

        assertEquals(1, result.size(), "Should return 1 article between 11:00 and 13:00");
    }
}