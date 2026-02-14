package rs.ac.ftn.isa.uploadapp.dto;

import java.io.Serializable;

/**
 * Event o novom videu – ista struktura kao u isa-backend (naziv, veličina, autor…).
 */
public class UploadEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long videoId;
    private String title;
    private long sizeBytes;
    private String authorId;
    private String authorUsername;
    private String createdAt;

    public UploadEvent() {
    }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
