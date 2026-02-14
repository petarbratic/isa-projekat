package rs.ac.ftn.isa.backend.dto;

public class StreamingChatMessageResponse {
    private String authorName;
    private String text;
    private long timestamp;

    public StreamingChatMessageResponse() {}

    public StreamingChatMessageResponse(String authorName, String text, long timestamp) {
        this.authorName = authorName;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
