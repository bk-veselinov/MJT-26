package bg.sofia.uni.fmi.mjt.newsfeed.http.utility;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Category;
import bg.sofia.uni.fmi.mjt.newsfeed.entity.Country;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;


class NewsFeedRequestTest {

    @Test
    void testBuilderThrowsOnNullKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> NewsFeedRequest.builder(null),
                "Should throw IllegalArgumentException when keywords are null");
    }

    @Test
    void testBuilderThrowsOnEmptyKeywords() {
        assertThrows(IllegalArgumentException.class,
                () -> NewsFeedRequest.builder(""),
                "Should throw IllegalArgumentException when keywords are empty");
    }

    @Test
    void testToQueryStringWithKeywordsOnly() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").build();
        String queryString = request.toQueryString();

        assertTrue(queryString.contains("q=test"), "Query string should contain q=test");
    }

    @Test
    void testToQueryStringWithCategory() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").category(Category.BUSINESS).build();
        String queryString = request.toQueryString();

        assertTrue(queryString.contains("q=test"), "Should contain query");
        assertTrue(queryString.contains("category=business"), "Should contain category");
    }

    @Test
    void testToQueryStringWithCountry() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").country(Country.US).build();
        String queryString = request.toQueryString();

        assertTrue(queryString.contains("country=us"), "Should contain country");
    }

    @Test
    void testToQueryStringWithPageSize() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").pageSize(50).build();
        String queryString = request.toQueryString();

        assertTrue(queryString.contains("pageSize=50"), "Should contain pageSize");
    }

    @Test
    void testToQueryStringThrowsOnPageSizeLimitExceed() {
        assertThrows(IllegalArgumentException.class,
                () -> NewsFeedRequest.builder("test").pageSize(101).build(),
                "Should throw IllegalArgumentException when pageSize exceed 100");
    }

    @Test
    void testToQueryStringWithPageCount() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").pageCount(5).build();
        String queryString = request.toQueryString();

        assertTrue(queryString.contains("page=5"), "Should contain page");
    }

    @Test
    void testToQueryStringWithAddKeyWords() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").addKeyWords("test1").build();
        String queryString = request.toQueryString();
        String expectedString = URLEncoder.encode("AND test1", StandardCharsets.UTF_8);

        assertTrue(queryString.contains("test"), "Should contain the keyword");
        assertTrue(queryString.contains(expectedString), "Should contain excluded word with NOT operator");
    }

    @Test
    void testToQueryStringWithExclusions() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").excludeKeyWords("exclude").build();
        String queryString = request.toQueryString();
        String expectedString = URLEncoder.encode("NOT exclude", StandardCharsets.UTF_8);

        assertTrue(queryString.contains("test"), "Should contain the keyword");
        assertTrue(queryString.contains(expectedString), "Should contain excluded word with NOT operator");
    }

    @Test
    void testToQueryStringWithExactPhrase() {
        NewsFeedRequest request = NewsFeedRequest.builder("test").addExactPhrase("phrase").build();
        String queryString = request.toQueryString();
        String expectedString = URLEncoder.encode("\"phrase\"", StandardCharsets.UTF_8);

        assertTrue(queryString.contains(expectedString), "Should contain exact phrase in quotes");
    }

    @Test
    void testToQueryStringThrowsOnLongQuery() {
        String longKeyword = "a".repeat(501);

        assertThrows(IllegalArgumentException.class,
                () -> NewsFeedRequest.builder(longKeyword).build(),
                "Should throw IllegalArgumentException when query is too long");
    }
}