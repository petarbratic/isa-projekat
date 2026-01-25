package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "VIDEO_POSTS")
public class VideoPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(
            name = "VIDEO_TAGS",
            joinColumns = @JoinColumn(name = "video_id")
    )
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "location")
    private String location;

    @Column(name = "views", nullable = false)
    private long views = 0;


    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getViews() {  return views;  }

    public void setViews(long views) { this.views = views; }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

}
