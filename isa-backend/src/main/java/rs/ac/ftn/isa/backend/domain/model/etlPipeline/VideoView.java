package rs.ac.ftn.isa.backend.domain.model.etlPipeline;


import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "VIDEO_VIEWS")
public class VideoView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "video_id")
    private Long videoId;

    @Column(name = "viewed_at")
    private Timestamp timestamp;

    public VideoView(Long videoId, Timestamp timestamp) {
        this.timestamp = timestamp;
        this.videoId = videoId;
    }

    public Long getId() {
        return id;
    }

}
