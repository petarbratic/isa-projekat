package rs.ac.ftn.isa.backend.service;

import rs.ac.ftn.isa.backend.domain.crdt.GCounterState;

import java.util.List;

public interface ViewCounterService {
    long incrementLocal(long videoId);
    long getTotal(long videoId);

    List<GCounterState> getDirtyStates();
    void markClean(List<Long> videoIds);

    void mergeIncoming(List<GCounterState> incoming);
}