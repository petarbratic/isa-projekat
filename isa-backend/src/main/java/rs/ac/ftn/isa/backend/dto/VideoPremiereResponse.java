package rs.ac.ftn.isa.backend.dto;

import java.sql.Timestamp;

public class VideoPremiereResponse {
    private boolean available;
    private String mode; // "WAIT", "HLS", "MP4"
    private String url;  // /media/... (m3u8 ili mp4)
    private long offsetSeconds;
    private Timestamp scheduledAt;
    private Timestamp serverNow;

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public long getOffsetSeconds() { return offsetSeconds; }
    public void setOffsetSeconds(long offsetSeconds) { this.offsetSeconds = offsetSeconds; }

    public Timestamp getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(Timestamp scheduledAt) { this.scheduledAt = scheduledAt; }

    public Timestamp getServerNow() { return serverNow; }
    public void setServerNow(Timestamp serverNow) { this.serverNow = serverNow; }
}