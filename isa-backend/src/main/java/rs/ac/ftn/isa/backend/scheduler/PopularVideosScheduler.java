package rs.ac.ftn.isa.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import rs.ac.ftn.isa.backend.service.PopularVideosEtlService;

@Component
public class PopularVideosScheduler {

    private final PopularVideosEtlService popularVideosEtlService;

    public PopularVideosScheduler(PopularVideosEtlService popularVideosEtlService) {
        this.popularVideosEtlService = popularVideosEtlService;
    }

    // Svaki dan u ...
    //@Scheduled(cron = "0 0 22 * * ?")
    // za testiranje
    @Scheduled(fixedRate = 30000)
    public void runDailyEtl() {
        popularVideosEtlService.runDailyPipeline();
    }
}