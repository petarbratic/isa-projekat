package rs.ac.ftn.isa.backend.domain.model.etlPipeline;

import jakarta.persistence.*;

@Entity
@Table(name = "POPULAR_VIDEO_ITEM")
public class PopularVideoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private Long videoId;

    @Column(name = "score", nullable = false)
    private Long score;

    @Column(name = "rank_position", nullable = false)
    private Integer rank;

    @ManyToOne
    @JoinColumn(name = "run_id", nullable = false)
    private PopularVideosRun run;

    protected PopularVideoItem() {}

    public PopularVideoItem(Long videoId, Long score, Integer rank, PopularVideosRun run) {
        this.videoId = videoId;
        this.score = score;
        this.rank = rank;
        this.run = run;
    }

    public Long getVideoId() { return videoId; }
    public Long getScore() { return score; }
    public Integer getRank() { return rank; }
}