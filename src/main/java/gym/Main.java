package gym;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Starts the gym service and uses it: calls every endpoint with HttpClient.
 */
public class Main {

    static final String URL = "http://localhost:8080/exercises";
    static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) throws Exception {
        new GymServer().start(8080);
        System.out.println("Service is running at " + URL);

        send(HttpRequest.newBuilder(URI.create(URL)).GET().build());
        send(HttpRequest.newBuilder(URI.create(URL)).POST(HttpRequest.BodyPublishers.ofString("Pull-up")).build());
        send(HttpRequest.newBuilder(URI.create(URL + "/4")).GET().build());
        send(HttpRequest.newBuilder(URI.create(URL + "/2")).DELETE().build());
        send(HttpRequest.newBuilder(URI.create(URL)).GET().build());

        // The service keeps running, so it can also be opened in a browser. Stop the program in IDEA.
    }

    // Sends a request and prints what the service answered
    static void send(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(request.method() + " " + request.uri() + " -> " + response.statusCode() + " " + response.body());
    }
}
