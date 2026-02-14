package rs.ac.ftn.isa.backend.service.uploadevent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.dto.UploadEvent;
import rs.ac.ftn.isa.backend.dto.UploadEventBenchmarkResult;
import rs.ac.ftn.isa.backend.proto.UploadEventProto;

import java.util.ArrayList;
import java.util.List;

/**
 * Benchmarks JSON vs Protobuf: average serialization time, deserialization time, and message size.
 * Runs on at least 50 messages.
 */
@Service
public class UploadEventBenchmarkService {

    private static final int MIN_MESSAGES = 50;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public UploadEventBenchmarkResult runBenchmark(int messageCount) {
        int n = Math.max(MIN_MESSAGES, messageCount);
        List<UploadEvent> events = createSampleEvents(n);

        UploadEventBenchmarkResult result = new UploadEventBenchmarkResult();
        result.setMessageCount(n);

        // JSON
        long[] jsonSerializeNs = new long[n];
        long[] jsonDeserializeNs = new long[n];
        long[] jsonSizes = new long[n];
        byte[][] jsonBytes = new byte[n][];

        for (int i = 0; i < n; i++) {
            UploadEvent e = events.get(i);
            long start = System.nanoTime();
            try {
                jsonBytes[i] = objectMapper.writeValueAsBytes(e);
            } catch (Exception ex) {
                throw new RuntimeException("JSON serialize", ex);
            }
            jsonSerializeNs[i] = System.nanoTime() - start;
            jsonSizes[i] = jsonBytes[i].length;
        }

        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            try {
                objectMapper.readValue(jsonBytes[i], UploadEvent.class);
            } catch (Exception ex) {
                throw new RuntimeException("JSON deserialize", ex);
            }
            jsonDeserializeNs[i] = System.nanoTime() - start;
        }

        result.setJsonAvgSerializeMs(avgNsToMs(jsonSerializeNs));
        result.setJsonAvgDeserializeMs(avgNsToMs(jsonDeserializeNs));
        result.setJsonAvgSizeBytes(avg(jsonSizes));

        // Protobuf
        long[] protoSerializeNs = new long[n];
        long[] protoDeserializeNs = new long[n];
        long[] protoSizes = new long[n];
        byte[][] protoBytes = new byte[n][];

        for (int i = 0; i < n; i++) {
            UploadEvent e = events.get(i);
            UploadEventProto.UploadEvent proto = UploadEventProducer.toProto(e);
            long start = System.nanoTime();
            protoBytes[i] = proto.toByteArray();
            protoSerializeNs[i] = System.nanoTime() - start;
            protoSizes[i] = protoBytes[i].length;
        }

        for (int i = 0; i < n; i++) {
            long start = System.nanoTime();
            try {
                UploadEventProto.UploadEvent.parseFrom(protoBytes[i]);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Protobuf deserialize", e);
            }
            protoDeserializeNs[i] = System.nanoTime() - start;
        }

        result.setProtobufAvgSerializeMs(avgNsToMs(protoSerializeNs));
        result.setProtobufAvgDeserializeMs(avgNsToMs(protoDeserializeNs));
        result.setProtobufAvgSizeBytes(avg(protoSizes));

        return result;
    }

    private List<UploadEvent> createSampleEvents(int count) {
        List<UploadEvent> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(new UploadEvent(
                    (long) (i + 1),
                    "Video title #" + i + " - Sample upload for benchmark",
                    1024L * 1024 * (i % 50 + 1),
                    "author-" + (i % 10),
                    "user" + (i % 20),
                    java.time.Instant.now().toString()
            ));
        }
        return list;
    }

    private static double avgNsToMs(long[] ns) {
        long sum = 0;
        for (long v : ns) sum += v;
        return (sum / (double) ns.length) / 1_000_000.0;
    }

    private static double avg(long[] values) {
        long sum = 0;
        for (long v : values) sum += v;
        return sum / (double) values.length;
    }
}
