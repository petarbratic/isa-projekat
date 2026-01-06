package rs.ac.ftn.isa.backend.dto;

import java.sql.Timestamp;
import java.util.List;

public class VideoPostResponse {

    private Long id;
    private String title;
    private String description;
    private List<String> tags;
    private String location;
    private Timestamp createdAt;

    private Long ownerId;
    private String ownerFullName;
    private long views;

    public VideoPostResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public long getViews() { return views;  }

    public void setViews(long views) { this.views = views; }

    public String getOwnerFullName() { return ownerFullName; }
    public void setOwnerFullName(String ownerFullName) { this.ownerFullName = ownerFullName; }
}