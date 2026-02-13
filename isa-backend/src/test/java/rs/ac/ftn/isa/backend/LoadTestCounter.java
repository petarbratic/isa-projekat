package rs.ac.ftn.isa.backend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadTestCounter {


    private static final List<String> REPLICAS = List.of(
            "http://localhost:8082",
            "http://localhost:8083"
            // "http://localhost:8084"
    );

    private static final long VIDEO_ID = 1L;

    private enum Mode { ROUND_ROBIN, RANDOM_UNIFORM }

    public static void main(String[] args) throws Exception {
        int totalRequests = 50;
        int burstConcurrency = 1; // za "po zahtevu" log drži 1
        int pauseMs = 50;         // mala pauza da scheduler stigne da "upadne"
        int waitForSyncMs = 6500;

        Mode mode = Mode.RANDOM_UNIFORM;

        if (REPLICAS.isEmpty()) {
            System.out.println("No replicas configured.");
            return;
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        System.out.println("=== CRDT G-Counter Load Test (Scheduler-based, per-request logging) ===");
        System.out.println("VideoId=" + VIDEO_ID);
        System.out.println("Total increments=" + totalRequests + ", burstConcurrency=" + burstConcurrency + ", mode=" + mode);
        System.out.println("Replicas (" + REPLICAS.size() + "): " + String.join(" | ", REPLICAS));

        System.out.println("\nInitial (gcounter state per replica):");
        printStateOnly(client);

        System.out.println("\nSending increments split across replicas (logging after EACH request)...");
        sendSplitIncrementsWithLogging(client, totalRequests, burstConcurrency, pauseMs, mode);

        System.out.println("\nImmediately after increments (likely BEFORE full sync):");
        printStateOnly(client);

        System.out.println("\nWaiting " + waitForSyncMs + "ms for scheduled CRDT sync...");
        Thread.sleep(waitForSyncMs);

        System.out.println("\nAfter scheduled sync (expected to converge):");
        printValueAndState(client);

        System.out.println("\nDone.");
    }

    private static void sendSplitIncrementsWithLogging(HttpClient client,
                                                       int totalRequests,
                                                       int burstConcurrency,
                                                       int pauseMs,
                                                       Mode mode) throws InterruptedException {

        Random rnd = new Random();
        int sent = 0;

        int n = REPLICAS.size();
        int[] perReplica = new int[n];

        while (sent < totalRequests) {
            int wave = Math.min(burstConcurrency, totalRequests - sent);
            List<Thread> threads = new ArrayList<>(wave);

            for (int i = 0; i < wave; i++) {
                int requestIndex = sent + i;

                int idx = (mode == Mode.ROUND_ROBIN)
                        ? (requestIndex % n)
                        : rnd.nextInt(n);

                perReplica[idx]++;

                String base = REPLICAS.get(idx);
                String url = base + "/counter/" + VIDEO_ID + "/inc";

                int reqNo = requestIndex + 1;
                Thread t = new Thread(() -> {
                    try {
                        post(client, url);
                        System.out.println("\n--- Request #" + reqNo + " -> R" + (idx + 1) + " (" + base + ") INC OK ---");
                    } catch (Exception e) {
                        System.out.println("\n--- Request #" + reqNo + " -> R" + (idx + 1) + " (" + base + ") INC FAILED: " + e.getMessage() + " ---");
                    }
                });

                threads.add(t);
                t.start();
            }

            for (Thread t : threads) t.join();

            sent += wave;

            printStateOnly(client);

            if (pauseMs > 0) Thread.sleep(pauseMs);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\nSent increments: total=").append(sent).append(" | per replica: ");
        for (int i = 0; i < n; i++) {
            sb.append("R").append(i + 1).append("=").append(perReplica[i]);
            if (i < n - 1) sb.append(", ");
        }
        System.out.println(sb);
    }

    private static void printStateOnly(HttpClient client) {
        for (int i = 0; i < REPLICAS.size(); i++) {
            String base = REPLICAS.get(i);
            String name = "R" + (i + 1) + " (" + base + ")";

            try {
                String stateJson = getJson(client, base + "/internal/crdt/gcounter/state");
                System.out.println(name + " gcounter state = " + stateJson);
            } catch (Exception e) {
                System.out.println(name + " STATE failed: " + e.getMessage());
            }

            if (i < REPLICAS.size() - 1) System.out.println();
        }
    }

    private static void printValueAndState(HttpClient client) {
        for (int i = 0; i < REPLICAS.size(); i++) {
            String base = REPLICAS.get(i);
            String name = "R" + (i + 1) + " (" + base + ")";

            try {
                long value = getLong(client, base + "/counter/" + VIDEO_ID);
                String stateJson = getJson(client, base + "/internal/crdt/gcounter/state");

                System.out.println(name + " value = " + value);
                System.out.println(name + " gcounter state = " + stateJson);
            } catch (Exception e) {
                System.out.println(name + " READ/STATE failed: " + e.getMessage());
            }

            if (i < REPLICAS.size() - 1) System.out.println();
        }
    }

    private static void post(HttpClient client, String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
        }
    }

    private static long getLong(HttpClient client, String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
        }
        return Long.parseLong(resp.body().trim());
    }

    private static String getJson(HttpClient client, String url) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + resp.statusCode() + " body=" + resp.body());
        }
        return resp.body().trim();
    }
}
