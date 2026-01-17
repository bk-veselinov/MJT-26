package bg.sofia.uni.fmi.mjt.newsfeed;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NewsFeedAPI {

    List<Article> getNewsByRequest(Request request);

    List<Article> getNewsByKeyWords(String... keyWords);

    Map<String, List<Article>> getNewsByAuthor(Request request);

    Map<String, List<Article>> getNewsByAuthor(String... keyWords);

    List<Article> getNewsBefore(LocalDateTime dateTime, Request request);

    List<Article> getNewsBefore(LocalDateTime dateTime, String... keyWords);

    List<Article> getNewsAfter(LocalDateTime dateTime, Request request);

    List<Article> getNewsAfter(LocalDateTime dateTime, String... keyWords);

    List<Article> getNewsBetween(LocalDateTime from, LocalDateTime to, Request request);

    List<Article> getNewsBetween(LocalDateTime from, LocalDateTime to, String... keyWords);

}
