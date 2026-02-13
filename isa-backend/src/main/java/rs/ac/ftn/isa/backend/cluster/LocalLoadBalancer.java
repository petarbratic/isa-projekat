package rs.ac.ftn.isa.backend.cluster;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Jednostavan load balancer na portu 8080 – round-robin na replike 8084 i 8085.
 * Pokreće se iz IntelliJ kao posebna Run konfiguracija (Application → Main class: ova klasa).
 * Ne koristi Spring – samo Java.
 */
public class LocalLoadBalancer {

    private static final String[] BACKENDS = { "http://localhost:8084", "http://localhost:8085" };
    private static final int PORT = 8080;
    private static final AtomicInteger next = new AtomicInteger(0);

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", LocalLoadBalancer::handle);
        server.setExecutor(null);
        server.start();
        System.out.println("Load balancer pokrenut na http://localhost:" + PORT);
        System.out.println("Provera: http://localhost:" + PORT + "/ping");
        System.out.println("Demo:    http://localhost:" + PORT + "/api/cluster/demo");
        System.out.println("Backend replike: " + String.join(", ", BACKENDS));
    }

    private static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getRawPath();

        // Sam load balancer – bez poziva na replike (provera da radi)
        if ("/".equals(path) || "/ping".equals(path)) {
            String msg = "Load balancer radi. Za API pozive koristi npr. http://localhost:" + PORT + "/api/cluster/demo";
            byte[] body = msg.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
            return;
        }

        String backend = BACKENDS[next.getAndIncrement() % BACKENDS.length];
        String query = exchange.getRequestURI().getRawQuery();
        String targetPath = path + (query != null && !query.isEmpty() ? "?" + query : "");

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(backend + targetPath))
                    .timeout(Duration.ofSeconds(10))
                    .method(exchange.getRequestMethod(), bodyPublisher(exchange));

            exchange.getRequestHeaders().forEach((name, values) -> {
                if (!skipHeader(name)) {
                    values.forEach(v -> reqBuilder.header(name, v));
                }
            });

            HttpResponse<byte[]> response = client.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
            response.headers().map().forEach((name, values) -> {
                if (!skipResponseHeader(name))
                    values.forEach(v -> exchange.getResponseHeaders().add(name, v));
            });
            exchange.sendResponseHeaders(response.statusCode(), response.body().length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(response.body());
            }
        } catch (Exception e) {
            System.err.println("Greška pri prosleđivanju na " + backend + targetPath + ": " + e.getMessage());
            String errMsg = "Proxy error (replike možda nisu pokrenute na 8084 i 8085): " + e.getMessage();
            byte[] err = errMsg.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(502, err.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(err); }
        }
    }

    private static boolean skipResponseHeader(String name) {
        String lower = name.toLowerCase();
        return "transfer-encoding".equals(lower) || "content-length".equals(lower);
    }

    private static boolean skipHeader(String name) {
        String lower = name.toLowerCase();
        return "host".equals(lower) || "connection".equals(lower)
                || "transfer-encoding".equals(lower) || "content-length".equals(lower);
    }

    private static HttpRequest.BodyPublisher bodyPublisher(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
            return HttpRequest.BodyPublishers.noBody();
        }
        try (InputStream in = exchange.getRequestBody(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            byte[] body = out.toByteArray();
            return body.length == 0 ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofByteArray(body);
        }
    }
}
