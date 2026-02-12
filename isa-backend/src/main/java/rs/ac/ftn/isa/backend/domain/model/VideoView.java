package rs.ac.ftn.isa.backend.domain.model;


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

    public VideoView(Timestamp timestamp, Long videoId) {
        this.timestamp = timestamp;
        this.videoId = videoId;
    }

    public Long getId() {
        return id;
    }

}
