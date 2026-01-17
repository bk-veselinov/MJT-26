package bg.sofia.uni.fmi.mjt.newsfeed.http.utility;

import bg.sofia.uni.fmi.mjt.newsfeed.entity.Category;
import bg.sofia.uni.fmi.mjt.newsfeed.entity.Country;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class NewsFeedRequest implements Request {

    private final List<String> queryFields;

    private NewsFeedRequest(RequestBuilder builder) {
        this.queryFields = builder.getQueryFields();
    }

    @Override
    public String toQueryString() {

        return String.join("&", queryFields);
    }

    public static RequestBuilder builder(String... keywords) {
        return new RequestBuilder(keywords);
    }

    public static class RequestBuilder {
        private static final int MAX_PAGE_SIZE = 100;
        private static final int MAX_SEARCH_WORDS_LENGTH = 500;

        private final Map<String, Object> queryData = new HashMap<>();

        private RequestBuilder(String... keyWords) {
            validateNotNull(keyWords);
            validateNotEmpty(keyWords);

            queryData.putIfAbsent("q=", Arrays.stream(keyWords).collect(Collectors.toSet()));
        }

        public NewsFeedRequest build() {
            return new NewsFeedRequest(this);
        }

        public List<String> getQueryFields() {
            List<String> queryFields = new ArrayList<>();
            for (Map.Entry<String, Object> data : queryData.entrySet()) {
                String fieldName = data.getKey();

                if (!fieldName.equals("q=")) {
                    queryFields.add(fieldName + data.getValue().toString());
                    continue;
                }

                String searchedWordsString = encodeString(String.join(" AND ", getCurrentKeyWords()));
                if (searchedWordsString.length() > MAX_SEARCH_WORDS_LENGTH) {
                    throw new IllegalArgumentException(
                            "The commutative length of all the key words must not exceed 500");
                }
                queryFields.add(fieldName + searchedWordsString);
            }

            return queryFields;
        }

        private String encodeString(String string) {
            return URLEncoder.encode(string, StandardCharsets.UTF_8);
        }

        public RequestBuilder category(Category category) {
            validateNotNull(category);
            queryData.putIfAbsent("category=", category);

            return this;
        }

        public RequestBuilder country(Country country) {
            validateNotNull(country);
            queryData.putIfAbsent("country=", country);

            return this;
        }

        public RequestBuilder addKeyWords(String... keyWords) {
            validateNotNull(keyWords);
            validateNotEmpty(keyWords);

            getCurrentKeyWords().addAll(Arrays.stream(keyWords).collect(Collectors.toSet()));

            return this;
        }

        public RequestBuilder excludeKeyWords(String... keyWords) {
            validateNotNull(keyWords);
            validateNotEmpty(keyWords);

            getCurrentKeyWords().addAll(Arrays.stream(keyWords).map(w -> "NOT " + w).collect(Collectors.toSet()));

            return this;
        }

        public RequestBuilder addExactPhrase(String phrase) {
            validateNotNull(phrase);

            getCurrentKeyWords().add("\"" + phrase + "\"");

            return this;
        }

        private Set<String> getCurrentKeyWords() {
            @SuppressWarnings("unchecked")
            //The value of "q=" will always be Set<String>. It is defined in the constructor
            Set<String> currentKeyWords = ((Set<String>) queryData.get("q="));

            return currentKeyWords;
        }

        public RequestBuilder pageSize(int pageSize) {
            if (pageSize > MAX_PAGE_SIZE) {
                throw new IllegalArgumentException("Page size cannot exceed 100");
            }
            queryData.putIfAbsent("pageSize=", pageSize);

            return this;
        }

        public RequestBuilder pageCount(int pageCount) {
            queryData.putIfAbsent("page=", pageCount);

            return this;
        }

        private void validateNotNull(Object obj) {
            if (obj == null) {
                throw new IllegalArgumentException("Cannot set property to null");
            }
        }

        private void validateNotEmpty(String... words) {
            if (Arrays.stream(words).filter(w -> !w.isBlank()).toList().isEmpty()) {
                throw new IllegalArgumentException("Cannot set property to null");
            }
        }
    }

}
