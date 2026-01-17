package bg.sofia.uni.fmi.mjt.newsfeed.http.utility;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Article;

public record HeadlinesBody(String status, int totalResults, Article[] articles) {
}
