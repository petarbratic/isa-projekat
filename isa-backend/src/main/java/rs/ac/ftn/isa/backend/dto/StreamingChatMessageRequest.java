package rs.ac.ftn.isa.backend.dto;

public class StreamingChatMessageRequest {
    private String text;
    private String token;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
