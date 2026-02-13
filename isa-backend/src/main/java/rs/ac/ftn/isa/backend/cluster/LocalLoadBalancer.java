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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Load balancer na portu 8080 – round-robin na replike 8084 i 8085.
 * Šalje samo na replike sa health=UP (parcijalni gubitak konekcije → isključuje neispravnu repliku).
 */
public class LocalLoadBalancer {

    private static final String[] BACKENDS = { "http://localhost:8084", "http://localhost:8085" };
    private static final int PORT = 8080;
    private static final AtomicInteger next = new AtomicInteger(0);
    private static final AtomicReferenceArray<Boolean> healthy = new AtomicReferenceArray<>(new Boolean[] { true, true });
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    public static void main(String[] args) throws IOException {
        startHealthCheckThread();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", LocalLoadBalancer::handle);
        server.setExecutor(null);
        server.start();
        System.out.println("Load balancer pokrenut na http://localhost:" + PORT);
        System.out.println("Health check: šalje samo na replike sa /actuator/health=UP");
        System.out.println("Demo: http://localhost:" + PORT + "/api/cluster/demo");
    }

    private static void startHealthCheckThread() {
        // Odmah prvi check
        for (int i = 0; i < BACKENDS.length; i++) {
            healthy.set(i, checkHealth(BACKENDS[i]));
        }
        Thread t = new Thread(() -> {
            while (true) {
                try { Thread.sleep(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
                for (int i = 0; i < BACKENDS.length; i++) {
                    healthy.set(i, checkHealth(BACKENDS[i]));
                }
            }
        }, "health-check");
        t.setDaemon(true);
        t.start();
    }

    private static boolean checkHealth(String baseUrl) {
        try {
            HttpResponse<String> r = httpClient.send(
                    HttpRequest.newBuilder().uri(URI.create(baseUrl + "/actuator/health")).timeout(Duration.ofSeconds(3)).GET().build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            String body = r.body();
            return r.statusCode() == 200 && body != null && body.contains("\"status\":\"UP\"");
        } catch (Exception e) {
            return false;
        }
    }

    private static String selectHealthyBackend() {
        for (int attempt = 0; attempt < BACKENDS.length; attempt++) {
            int idx = next.getAndIncrement() % BACKENDS.length;
            if (Boolean.TRUE.equals(healthy.get(idx))) return BACKENDS[idx];
        }
        return null;
    }

    private static void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getRawPath();

        if ("/".equals(path) || "/ping".equals(path)) {
            StringBuilder sb = new StringBuilder("Load balancer radi. Health check uključen – šalje samo na UP replike.\n");
            for (int i = 0; i < BACKENDS.length; i++) {
                sb.append("  ").append(BACKENDS[i]).append(": ").append(Boolean.TRUE.equals(healthy.get(i)) ? "UP" : "DOWN").append("\n");
            }
            byte[] body = sb.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(body); }
            return;
        }

        String backend = selectHealthyBackend();
        if (backend == null) {
            String err = "Nijedna replika nije UP (parcijalni gubitak konekcije – obe nema bazu?)";
            byte[] b = err.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(503, b.length);
            try (OutputStream out = exchange.getResponseBody()) { out.write(b); }
            return;
        }
        String query = exchange.getRequestURI().getRawQuery();
        String targetPath = path + (query != null && !query.isEmpty() ? "?" + query : "");
        try {
            HttpRequest.Builder reqBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(backend + targetPath))
                    .timeout(Duration.ofSeconds(10))
                    .method(exchange.getRequestMethod(), bodyPublisher(exchange));

            exchange.getRequestHeaders().forEach((name, values) -> {
                if (!skipHeader(name)) {
                    values.forEach(v -> reqBuilder.header(name, v));
                }
            });

            HttpResponse<byte[]> response = httpClient.send(reqBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
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
