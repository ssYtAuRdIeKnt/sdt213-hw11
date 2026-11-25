package gym;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple REST web service that keeps a list of gym exercises in memory.
 */
public class GymServer {

    private final Map<Integer, String> exercises = new LinkedHashMap<>();
    private int nextId = 1;

    public void start(int port) throws IOException {
        exercises.put(nextId++, "Bench press");
        exercises.put(nextId++, "Squat");
        exercises.put(nextId++, "Deadlift");

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/exercises", this::handle);
        server.start();
    }

    // Called for every request to /exercises
    private void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        // "/exercises/2" -> ["", "exercises", "2"]
        String[] parts = exchange.getRequestURI().getPath().split("/");

        if (method.equals("GET") && parts.length == 2) {
            send(exchange, 200, exercises.toString());
        } else if (method.equals("GET")) {
            int id;
            try {
                id = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                // The id in the URL is not a number, e.g. "/exercises/abc"
                send(exchange, 400, "Bad request: id must be a number");
                return;
            }
            String name = exercises.get(id);
            if (name == null) {
                send(exchange, 404, "Not found");
            } else {
                send(exchange, 200, name);
            }
        } else if (method.equals("POST")) {
            String name = new String(exchange.getRequestBody().readAllBytes());
            int id = nextId++;
            exercises.put(id, name);
            send(exchange, 201, "Added with id " + id);
        } else if (method.equals("DELETE")) {
            exercises.remove(Integer.parseInt(parts[2]));
            send(exchange, 200, "Deleted");
        } else {
            send(exchange, 405, "Method not allowed");
        }
    }

    // Sends the status code and the text back to the client
    private void send(HttpExchange exchange, int status, String text) throws IOException {
        byte[] bytes = text.getBytes();
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
