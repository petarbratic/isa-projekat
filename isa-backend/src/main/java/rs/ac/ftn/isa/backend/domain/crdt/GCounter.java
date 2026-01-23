package rs.ac.ftn.isa.backend.domain.crdt;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// G Counter - Grow Only Counter
// CRDT - Conflict Free Replicated Data Types (to the Rescue)
// Final klasa - ne moze se nasledjivati
public final class GCounter implements Serializable {

    // Ovde se cuva broj pregleda svake replike
    private final Map<String, Long> counts;

    // Konstruktor
    public GCounter() {
        this.counts = new HashMap<>();
    }

    // Copy construtor u sustini
    public GCounter(Map<String, Long> initialCounts) {
        this.counts = new HashMap<>();
        if (initialCounts != null) {
            // Entry znaci par tj. jedan red, EntrySet su svi podaci
            // keys su samo kljucevi, values samo vrednosti
            for (Map.Entry<String, Long> e : initialCounts.entrySet()) {
                String id = e.getKey();
                // safeNonNegative je pomocna metoda
                long v = safeNonNegative(e.getValue());
                if (id != null && !id.isBlank()) {
                    this.counts.put(id, v);
                }
            }
        }
    }

    // Inkrement za 1
    public void increment(String replicaId) {
        incrementBy(replicaId, 1L);
    }

    // Inkrement za prosledjenu vrednost
    public void incrementBy(String replicaId, long delta) {
        validateReplicaId(replicaId);
        if (delta <= 0) throw new IllegalArgumentException("delta must be > 0");
        long current = counts.getOrDefault(replicaId, 0L);
        counts.put(replicaId, current + delta);
    }

    // Merge metoda
    public void merge(GCounter other) {
        if (other == null) return;
        mergeState(other.counts);
    }

    // MergeState
    public void mergeState(Map<String, Long> otherCounts) {

        if (otherCounts == null || otherCounts.isEmpty()) return;

        Set<Map.Entry<String, Long>> entries = otherCounts.entrySet();
        for (Map.Entry<String, Long> e : entries) {
            String id = e.getKey();
            if (id == null || id.isBlank()) continue;
            long remote = safeNonNegative(e.getValue());
            long local = counts.getOrDefault(id, 0L);
            if (remote > local) {
                counts.put(id, remote);
            }
        }
    }

    /**
     * Returns the total value (sum of all replica slots).
     */
    // U bazi se nikada nece cuvati zbir broja pregleda,
    // nego ce se uvek sabirati brojevi pregleda iz obe
    // (vise) replika
    public long value() {
        long sum = 0L;
        for (long v : counts.values()) sum += v;
        return sum;
    }

    /**
     * Exposes an immutable snapshot of the internal state.
     */
    // U sustini getter. Vracena mapa je neizmenjiva
    public Map<String, Long> snapshot() {
        return Collections.unmodifiableMap(new HashMap<>(counts));
    }

    private static void validateReplicaId(String replicaId) {
        if (replicaId == null || replicaId.isBlank()) {
            throw new IllegalArgumentException("replicaId must not be null/blank");
        }
    }

    private static long safeNonNegative(Long v) {
        if (v == null) return 0L;
        if (v < 0) throw new IllegalArgumentException("Counter values must be >= 0");
        return v;
    }

    // Preklapanje operatora
    // Dve istance klase GCounter su jednake ako su jednake
    // counts mape
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GCounter)) return false;
        GCounter gCounter = (GCounter) o;
        return Objects.equals(counts, gCounter.counts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts);
    }

    @Override
    public String toString() {
        return "GCounter{counts=" + counts + ", value=" + value() + '}';
    }
}
