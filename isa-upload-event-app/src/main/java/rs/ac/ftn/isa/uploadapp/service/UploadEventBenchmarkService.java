package rs.ac.ftn.isa.uploadapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.uploadapp.dto.UploadEvent;
import rs.ac.ftn.isa.uploadapp.dto.UploadEventBenchmarkResult;
import rs.ac.ftn.isa.uploadapp.proto.UploadEventProto;

import java.util.ArrayList;
import java.util.List;

/**
 * Poređenje JSON vs Protobuf: prosečno vreme serijalizacije, deserijalizacije i veličina poruke.
 * Na bar 50 poruka.
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
            UploadEventProto.UploadEvent proto = toProto(e);
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

    private static UploadEventProto.UploadEvent toProto(UploadEvent dto) {
        if (dto == null) return UploadEventProto.UploadEvent.getDefaultInstance();
        return UploadEventProto.UploadEvent.newBuilder()
                .setVideoId(dto.getVideoId() != null ? dto.getVideoId() : 0L)
                .setTitle(dto.getTitle() != null ? dto.getTitle() : "")
                .setSizeBytes(dto.getSizeBytes())
                .setAuthorId(dto.getAuthorId() != null ? dto.getAuthorId() : "")
                .setAuthorUsername(dto.getAuthorUsername() != null ? dto.getAuthorUsername() : "")
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : "")
                .build();
    }

    private List<UploadEvent> createSampleEvents(int count) {
        List<UploadEvent> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            UploadEvent e = new UploadEvent();
            e.setVideoId((long) (i + 1));
            e.setTitle("Video title #" + i + " - Sample upload for benchmark");
            e.setSizeBytes(1024L * 1024 * (i % 50 + 1));
            e.setAuthorId("author-" + (i % 10));
            e.setAuthorUsername("user" + (i % 20));
            e.setCreatedAt(java.time.Instant.now().toString());
            list.add(e);
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
