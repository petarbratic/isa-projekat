package rs.ac.ftn.isa.backend.dto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Event published when a new video is uploaded (YouTube-like).
 * Used for message queue in both JSON and Protobuf formats.
 */
public class UploadEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long videoId;
    private String title;
    private long sizeBytes;
    private String authorId;
    private String authorUsername;
    private String createdAt; // ISO-8601 string for compatibility

    public UploadEvent() {
    }

    public UploadEvent(Long videoId, String title, long sizeBytes, String authorId, String authorUsername, String createdAt) {
        this.videoId = videoId;
        this.title = title;
        this.sizeBytes = sizeBytes;
        this.authorId = authorId;
        this.authorUsername = authorUsername;
        this.createdAt = createdAt;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
