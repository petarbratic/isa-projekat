package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import rs.ac.ftn.isa.backend.persistence.StringLongMapJsonConverter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "view_counter")
public class ViewCounter {

    @Id
    @Column(name = "video_id")
    private Long id; // OVDE je videoId

    @Convert(converter = StringLongMapJsonConverter.class)
    @Column(name = "counts_json", nullable = false, columnDefinition = "TEXT")
    private Map<String, Long> counts = new HashMap<>();

    @Column(name = "dirty", nullable = false)
    private boolean dirty = false;

    @Column(name = "updated_at_utc", nullable = false)
    private Instant updatedAtUtc = Instant.EPOCH;

    protected ViewCounter() { }

    public ViewCounter(Long videoId) {
        this.id = videoId;
        this.updatedAtUtc = Instant.now();
    }

    public Long getId() { return id; }

    public Map<String, Long> getCounts() { return counts; }
    public void setCounts(Map<String, Long> counts) { this.counts = counts; }

    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }

    public Instant getUpdatedAtUtc() { return updatedAtUtc; }
    public void setUpdatedAtUtc(Instant updatedAtUtc) { this.updatedAtUtc = updatedAtUtc; }
}