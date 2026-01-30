package rs.ac.ftn.isa.backend.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.domain.crdt.GCounter;
import rs.ac.ftn.isa.backend.domain.crdt.GCounterState;
import rs.ac.ftn.isa.backend.domain.model.ViewCounter;
import rs.ac.ftn.isa.backend.repository.ViewCounterRepository;
import rs.ac.ftn.isa.backend.service.ViewCounterService;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Profile({"replica1", "replica2"})
@Service
public class ViewCounterServiceImpl implements ViewCounterService {

    private final ViewCounterRepository repo;
    private final String replicaId;

    public ViewCounterServiceImpl(ViewCounterRepository repo,
                                  @Value("${crdt.replica-id}") String replicaId) {
        this.repo = repo;
        this.replicaId = replicaId;
    }

    private ViewCounter load(long videoId) {
        return repo.findById(videoId).orElseGet(() -> repo.save(new ViewCounter(videoId)));
    }

    @Override
    @Transactional
    public long incrementLocal(long videoId) {
        ViewCounter row = load(videoId);

        GCounter gc = new GCounter(row.getCounts());
        gc.increment(replicaId);

        row.setCounts(gc.snapshot());
        row.setDirty(true);
        row.setUpdatedAtUtc(Instant.now());

        return gc.value();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotal(long videoId) {
        ViewCounter row = load(videoId);
        return new GCounter(row.getCounts()).value();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GCounterState> getDirtyStates() {
        List<ViewCounter> dirty = repo.findByDirtyTrue();
        List<GCounterState> out = new ArrayList<>(dirty.size());
        for (ViewCounter v : dirty) {
            out.add(new GCounterState(v.getId(), v.getCounts()));
        }
        return out;
    }

    @Override
    @Transactional
    public void markClean(List<Long> videoIds) {
        if (videoIds == null || videoIds.isEmpty()) return;
        for (Long id : videoIds) {
            ViewCounter row = load(id);
            row.setDirty(false);
            row.setUpdatedAtUtc(Instant.now());
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<GCounterState> exportStates() {
        List<ViewCounter> all = repo.findAll();
        List<GCounterState> out = new ArrayList<>(all.size());
        for (ViewCounter v : all) {
            out.add(new GCounterState(v.getId(), v.getCounts()));
        }
        return out;
    }
    @Override
    @Transactional
    public void mergeIncoming(List<GCounterState> incoming) {
        if (incoming == null || incoming.isEmpty()) return;

        for (GCounterState st : incoming) {
            long videoId = st.getVideoId();
            ViewCounter localRow = load(videoId);

            GCounter local = new GCounter(localRow.getCounts());
            local.mergeState(st.getCounts());

            localRow.setCounts(local.snapshot());
            localRow.setUpdatedAtUtc(Instant.now());
        }
    }
}