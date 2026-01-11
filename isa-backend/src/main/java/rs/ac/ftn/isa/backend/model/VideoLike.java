package rs.ac.ftn.isa.backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(
        name = "VIDEO_LIKES",
        uniqueConstraints = @UniqueConstraint(name = "uk_video_likes_user_video", columnNames = {"user_id", "video_id"})
)
public class VideoLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private VideoPost video;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public VideoPost getVideo() { return video; }
    public void setVideo(VideoPost video) { this.video = video; }

    public Timestamp getCreatedAt() { return createdAt; }
}
