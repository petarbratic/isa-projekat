package rs.ac.ftn.isa.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.domain.model.etlPipeline.PopularVideoItem;
import rs.ac.ftn.isa.backend.domain.model.etlPipeline.PopularVideosRun;
import rs.ac.ftn.isa.backend.repository.etlPipeline.PopularVideoItemRepository;
import rs.ac.ftn.isa.backend.repository.etlPipeline.PopularVideosRunRepository;
import rs.ac.ftn.isa.backend.repository.etlPipeline.VideoViewRepository;
import rs.ac.ftn.isa.backend.repository.etlPipeline.projection.DailyVideoViewsRow;
import rs.ac.ftn.isa.backend.service.PopularVideosEtlService;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class PopularVideosEtlServiceImpl implements PopularVideosEtlService {

    private final VideoViewRepository videoViewRepository;
    private final PopularVideosRunRepository popularVideosRunRepository;
    private final PopularVideoItemRepository popularVideoItemRepository;

    public PopularVideosEtlServiceImpl(
            VideoViewRepository videoViewRepository,
            PopularVideosRunRepository popularVideosRunRepository,
            PopularVideoItemRepository popularVideoItemRepository
    ) {
        this.videoViewRepository = videoViewRepository;
        this.popularVideosRunRepository = popularVideosRunRepository;
        this.popularVideoItemRepository = popularVideoItemRepository;
    }

    private static class TopItem {
        final int rank;
        final long videoId;
        final long score;

        TopItem(int rank, long videoId, long score) {
            this.rank = rank;
            this.videoId = videoId;
            this.score = score;
        }
    }

    @Override
    @Transactional
    public void runDailyPipeline() {
        // Extract: poslednjih 7 celih dana (bez današnjeg dana)
        LocalDate today = LocalDate.now();
        LocalDateTime toLdt = today.atStartOfDay();     // danas 00:00
        LocalDateTime fromLdt = toLdt.minusDays(7);     // pre 7 dana 00:00

        Timestamp from = Timestamp.valueOf(fromLdt);
        Timestamp to = Timestamp.valueOf(toLdt);

        List<DailyVideoViewsRow> rows = videoViewRepository.findDailyViews(from, to);

        // Transform: racunanje popularity score-a po videu
        Map<Long, Long> scoreByVideoId = new HashMap<>();

        for (DailyVideoViewsRow row : rows) {
            Long videoId = row.getVideoId();
            LocalDate day = row.getDay().toLocalDate();
            long views = row.getViews() == null ? 0L : row.getViews();

            long daysAgo = ChronoUnit.DAYS.between(day, today); // juče => 1
            long weight = 8 - daysAgo;                         // juče => 7, pre 7 dana => 1

            long add = views * weight;
            scoreByVideoId.merge(videoId, add, Long::sum);
        }

        // Sort + Top 3
        List<Map.Entry<Long, Long>> sorted = new ArrayList<>(scoreByVideoId.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        List<TopItem> top3 = new ArrayList<>();
        for (int i = 0; i < Math.min(3, sorted.size()); i++) {
            Map.Entry<Long, Long> e = sorted.get(i);
            top3.add(new TopItem(i + 1, e.getKey(), e.getValue()));
        }

        // Load: upis run-a + stavki (top3)
        PopularVideosRun run = popularVideosRunRepository.save(
                new PopularVideosRun(Timestamp.from(Instant.now()))
        );

        if (!top3.isEmpty()) {
            List<PopularVideoItem> items = new ArrayList<>(top3.size());
            for (TopItem item : top3) {
                items.add(new PopularVideoItem(item.videoId, item.score, item.rank, run));
            }
            popularVideoItemRepository.saveAll(items);
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getLatestTop3VideoIds() {

        Optional<PopularVideosRun> latestRunOpt =
                popularVideosRunRepository.findTopByOrderByExecutedAtDesc();

        if (latestRunOpt.isEmpty()) {
            return Collections.emptyList();
        }

        PopularVideosRun latestRun = latestRunOpt.get();

        List<PopularVideoItem> items =
                popularVideoItemRepository.findByRunOrderByRankAsc(latestRun);

        List<Long> videoIds = new ArrayList<>();
        for (PopularVideoItem item : items) {
            videoIds.add(item.getVideoId());
        }

        return videoIds;
    }
}