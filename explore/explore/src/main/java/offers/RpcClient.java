package offers;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.net.http.HttpRequest.BodyPublishers.*;
import static java.net.http.HttpRequest.newBuilder;

public class RpcClient {

    private final Duration timeout;
    private final Duration duration = Duration.ofMinutes(2);

    public RpcClient(Duration timeout) {
        this.timeout = timeout;
    }

    public RpcClient() {
        this(Duration.ofSeconds(20));
    }

    public HttpResponse<String> send(String api, Object body) {

        HttpClient client = createClient();
        String payload = new Gson().toJson(body);


        HttpRequest request = newBuilder()
                .uri(URI.create(api))
                .timeout(duration)
                .header("Content-Type", "application/json")
                .POST(ofByteArray(payload.getBytes()))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClient createClient() {
        return HttpClient
                .newBuilder()
                .version(Version.HTTP_1_1)
                .connectTimeout(timeout)
                .build();
    }
}
