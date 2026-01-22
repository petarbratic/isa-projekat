package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

import rs.ac.ftn.isa.backend.domain.enums.TranscodingStatus;

@Entity
@Table(name = "TRANSCODING_JOBS", uniqueConstraints = {
        @UniqueConstraint(columnNames = "message_id")
})
public class TranscodingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "video_post_id", nullable = false)
    private VideoPost videoPost;

    @Column(name = "message_id", nullable = false)
    private String messageId;

    @Column(name = "input_path", nullable = false)
    private String inputPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TranscodingStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "started_at")
    private Timestamp startedAt;

    @Column(name = "finished_at")
    private Timestamp finishedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount = 0;

    @PrePersist
    public void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        if (status == null) status = TranscodingStatus.PENDING;
    }
}
