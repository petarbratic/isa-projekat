package rs.ac.ftn.isa.backend.domain.crdt;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * DTO/state wrapper used for exchanging GCounter state between replicas.
 * Keep it simple: videoId + per-replica counts map.
 */
public final class GCounterState implements Serializable {

    private long videoId;
    private Map<String, Long> counts = new HashMap<>();

    public GCounterState() {
    }

    public GCounterState(long videoId, Map<String, Long> counts) {
        this.videoId = videoId;
        if (counts != null) this.counts = new HashMap<>(counts);
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public Map<String, Long> getCounts() {
        return counts;
    }

    public void setCounts(Map<String, Long> counts) {
        this.counts = (counts == null) ? new HashMap<>() : new HashMap<>(counts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GCounterState)) return false;
        GCounterState that = (GCounterState) o;
        return videoId == that.videoId && Objects.equals(counts, that.counts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, counts);
    }

    @Override
    public String toString() {
        return "GCounterState{videoId=" + videoId + ", counts=" + counts + '}';
    }
}
