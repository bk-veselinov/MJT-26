package bg.sofia.uni.fmi.mjt.newsfeed.http.validator;

import java.net.http.HttpResponse;

public interface ResponseValidator {

    void validate(HttpResponse<String> data);
}
