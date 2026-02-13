package rs.ac.ftn.isa.backend.api;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Demo API za dokaz rada u klasteru: load balancer šalje zahteve na replike,
 * odgovor sadrži identifikator replike i vreme (primer proizvoljnog API-ja).
 */
@RestController
@RequestMapping(value = "/api/cluster", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClusterController {

    @Value("${app.replica.id:${SERVER_PORT:unknown}}")
    private String replicaId;

    /**
     * Primer proizvoljnog API-ja za klaster.
     * Load balancer round-robin šalje zahteve na replike; odgovor pokazuje koja replika je odgovorila.
     * Korisno za: pad replike, ponovno podizanje, proveru health check-a.
     */
    @GetMapping("/demo")
    public Map<String, Object> demo() {
        return Map.of(
                "replicaId", replicaId,
                "timestamp", Instant.now().toString(),
                "message", "Klaster radi. Zahtev obradila replika: " + replicaId
        );
    }
}
