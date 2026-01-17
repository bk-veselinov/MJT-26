package bg.sofia.uni.fmi.mjt.newsfeed.http;

import bg.sofia.uni.fmi.mjt.newsfeed.http.parser.JsonResponseParser;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.EndPointContext;
import bg.sofia.uni.fmi.mjt.newsfeed.http.utility.Request;
import bg.sofia.uni.fmi.mjt.newsfeed.http.validator.ResponseValidator;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpCaller implements HttpCallerInterface {

    private final HttpClient client;

    private final ResponseValidator validator;
    private final EndPointContext endPointContext;

    public HttpCaller(EndPointContext endPointContext, ResponseValidator validator) {
        this.endPointContext = endPointContext;
        this.validator = validator;
        this.client = HttpClient.newHttpClient();
    }

    HttpCaller(EndPointContext endPointContext, ResponseValidator validator, HttpClient client) {
        this.endPointContext = endPointContext;
        this.validator = validator;
        this.client = client;
    }

    @Override
    public String getResponseAsJson(Request request) {
        return executeRequest(request).body();
    }

    @Override
    public <T, R extends Request> T getParsedObject(R request, JsonResponseParser<T> parser) {
        return parser.parseResponse(executeRequest(request));
    }

    private HttpResponse<String> executeRequest(Request request) {
        try {
            URI uri = new URI(endPointContext.scheme(), endPointContext.host(), endPointContext.path(),
                    request.toQueryString(), null);

            HttpRequest httpRequest = HttpRequest.newBuilder(uri).headers(endPointContext.headers()).build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            validator.validate(response);

            return response;

        } catch (IOException e) {
            throw new RuntimeException("Problem during I/O operation", e);

        } catch (InterruptedException e) {
            throw new RuntimeException("The request was interrupted", e);

        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URI", e);
        }
    }

}
