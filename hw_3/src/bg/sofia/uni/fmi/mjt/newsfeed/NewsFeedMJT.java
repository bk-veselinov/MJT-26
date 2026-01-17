package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.http.HttpCaller;
import bg.sofia.uni.fmi.mjt.newsfeed.http.HttpCallerInterface;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonArticlesParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.EndPointContext;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.NewsFeedRequest;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;
import bg.sofia.uni.fmi.mjt.newsfeed.http.validator.NewsFeedResponseValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewsFeedMJT implements NewsFeedAPI {

    private static final String SCHEME = "https";
    private static final String HOST = "newsapi.org";
    private static final String PATH = "/v2/top-headlines";
    private static final String API_HEADER = "x-api-key";

    private final JsonResponseParser<List<Article>> articlesParser;
    private final HttpCallerInterface caller;

    public NewsFeedMJT(String apiKey) {
        articlesParser = new JsonArticlesParser();

        EndPointContext endPoint = new EndPointContext(SCHEME, HOST, PATH, API_HEADER, apiKey);
        caller = new HttpCaller(endPoint, new NewsFeedResponseValidator());
    }

    NewsFeedMJT(HttpCallerInterface caller, JsonResponseParser<List<Article>> parser) {
        this.articlesParser = parser;
        this.caller = caller;
    }

    @Override
    public List<Article> getNewsByRequest(Request request) {
        return caller.getParsedObject(request, articlesParser);
    }

    @Override
    public List<Article> getNewsByKeyWords(String... keyWords) {
        return getNewsByRequest(getRequestByKeyWords(keyWords));
    }

    @Override
    public Map<String, List<Article>> getNewsByAuthor(Request request) {
        return getNewsByRequest(request).stream().collect(Collectors.groupingBy(Article::author));
    }

    @Override
    public Map<String, List<Article>> getNewsByAuthor(String... keyWords) {
        return getNewsByAuthor(getRequestByKeyWords(keyWords));
    }

    @Override
    public List<Article> getNewsBefore(LocalDateTime dateTime, Request request) {
        return getNewsByRequest(request).stream()
                .filter(a -> !LocalDateTime.parse(
                        a.publishedAt(), DateTimeFormatter.ISO_DATE_TIME
                ).isAfter(dateTime))
                .toList();
    }

    @Override
    public List<Article> getNewsBefore(LocalDateTime dateTime, String... keyWords) {
        return getNewsBefore(dateTime, getRequestByKeyWords(keyWords));
    }

    @Override
    public List<Article> getNewsAfter(LocalDateTime dateTime, Request request) {
        return getNewsByRequest(request).stream()
                .filter(a -> !LocalDateTime.parse(
                        a.publishedAt(), DateTimeFormatter.ISO_DATE_TIME
                ).isBefore(dateTime))
                .toList();
    }

    @Override
    public List<Article> getNewsAfter(LocalDateTime dateTime, String... keyWords) {
        return getNewsAfter(dateTime, getRequestByKeyWords(keyWords));
    }

    @Override
    public List<Article> getNewsBetween(LocalDateTime from, LocalDateTime to, Request request) {
        validateTimeFrame(from, to);

        return getNewsByRequest(request).stream()
                .filter(a -> {
                    LocalDateTime date = LocalDateTime.parse(
                            a.publishedAt(), DateTimeFormatter.ISO_DATE_TIME
                    );
                    return !date.isAfter(to) && !date.isBefore(from);
                })
                .toList();
    }

    @Override
    public List<Article> getNewsBetween(LocalDateTime from, LocalDateTime to, String... keyWords) {
        validateTimeFrame(from, to);

        return getNewsBetween(from, to, getRequestByKeyWords(keyWords));
    }

    private Request getRequestByKeyWords(String... keyWords) {
        return NewsFeedRequest.builder(keyWords).build();
    }

    private void validateNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Null was passed as a parameter");
        }
    }

    private void validateTimeFrame(LocalDateTime from, LocalDateTime to) {
        validateNotNull(from);
        validateNotNull(to);

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid time frame. From must be before To");
        }
    }

}
