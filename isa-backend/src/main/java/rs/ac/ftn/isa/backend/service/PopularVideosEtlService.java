package rs.ac.ftn.isa.backend.service;

import rs.ac.ftn.isa.backend.domain.model.VideoView;

public interface PopularVideosEtlService {
    void runDailyPipeline();
}
