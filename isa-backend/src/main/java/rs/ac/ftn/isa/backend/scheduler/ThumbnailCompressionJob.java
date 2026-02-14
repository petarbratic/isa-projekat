package rs.ac.ftn.isa.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.ThumbnailCompressionService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ThumbnailCompressionJob {

    private final VideoPostRepository videoPostRepository;
    private final ThumbnailCompressionService compressionService;

    public ThumbnailCompressionJob(VideoPostRepository videoPostRepository,
                                   ThumbnailCompressionService compressionService) {
        this.videoPostRepository = videoPostRepository;
        this.compressionService = compressionService;
    }

    // svaki dan u 02:00
    @Scheduled(cron = "0 0 2 * * *")
    // @Scheduled(cron = "*/30 * * * * *")
    @Transactional
    public void compressOldThumbnails() {
        Timestamp cutoff = Timestamp.from(Instant.now().minus(30, ChronoUnit.DAYS));
        List<VideoPost> posts =
                videoPostRepository.findByThumbnailCompressedPathIsNullAndCreatedAtBefore(cutoff);

        for (VideoPost post : posts) {
            try {
                boolean done = compressionService.compressThumbnail(post);
                if (done) videoPostRepository.save(post);
            } catch (Exception e) {
                System.out.println("Compression failed for post " + post.getId() + ": " + e.getMessage());
            }
        }
    }
}