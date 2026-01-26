package rs.ac.ftn.isa.backend.service.sync;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import rs.ac.ftn.isa.backend.domain.crdt.GCounterState;
import rs.ac.ftn.isa.backend.service.ViewCounterService;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Profile({"replica1", "replica2"})
@Component
public class ViewCounterSyncScheduler {

    private final ViewCounterService service;
    private final RestTemplate restTemplate;
    private final List<String> peers;

    public ViewCounterSyncScheduler(ViewCounterService service,
                                    RestTemplate restTemplate,
                                    @Value("${crdt.peers}") String peersCsv) {
        this.service = service;
        this.restTemplate = restTemplate;
        this.peers = parse(peersCsv);
    }

    @Scheduled(fixedDelayString = "${crdt.sync-period-ms:2000}")
    public void sync() {
        if (peers.isEmpty()) return;

        List<GCounterState> dirty = service.getDirtyStates();
        if (dirty.isEmpty()) return;

        boolean allOk = true;
        for (String peer : peers) {
            try {
                restTemplate.postForEntity(peer + "/internal/crdt/gcounter/push", dirty, Void.class);
            } catch (Exception ex) {
                allOk = false;
            }
        }

        if (allOk) {
            List<Long> ids = new ArrayList<>(dirty.size());
            for (GCounterState s : dirty) ids.add(s.getVideoId());
            service.markClean(ids);
        }
    }

    private static List<String> parse(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null || csv.isBlank()) return out;
        for (String p : csv.split(",")) {
            String v = p.trim();
            if (!v.isBlank()) out.add(v);
        }
        return out;
    }
}