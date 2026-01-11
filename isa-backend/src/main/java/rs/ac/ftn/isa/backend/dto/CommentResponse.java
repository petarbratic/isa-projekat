package rs.ac.ftn.isa.backend.dto;

import java.sql.Timestamp;

public class CommentResponse {
    private Long id;
    private Long authorId;
    private String authorEmail;
    private String authorName;
    private String text;
    private Timestamp createdAt;

    public CommentResponse() {}

    public CommentResponse(Long id, Long authorId, String authorEmail, String authorName, String text, Timestamp createdAt) {
        this.id = id;
        this.authorId = authorId;
        this.authorEmail = authorEmail;
        this.authorName = authorName;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getAuthorEmail() { return authorEmail; }
    public String getAuthorName() { return authorName; }
    public String getText() { return text; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Long getAuthorId() { return authorId; }

    public void setId(Long id) { this.id = id; }
    public void setAuthorEmail(String authorEmail) { this.authorEmail = authorEmail; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setText(String text) { this.text = text; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}