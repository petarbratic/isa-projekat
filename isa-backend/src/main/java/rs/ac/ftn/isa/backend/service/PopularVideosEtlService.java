package rs.ac.ftn.isa.backend.service;

import java.util.List;

public interface PopularVideosEtlService {
    void runDailyPipeline();
    List<Long> getLatestTop3VideoIds();
}
