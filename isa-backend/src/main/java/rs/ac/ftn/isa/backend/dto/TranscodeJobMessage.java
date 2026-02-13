package rs.ac.ftn.isa.backend.dto;

import java.io.Serializable;
import java.util.UUID;

public class TranscodeJobMessage implements Serializable {
    private String jobId;
    private Long videoId;
    private String inputPath;
    private String preset;

    public TranscodeJobMessage() { }

    public TranscodeJobMessage(Long videoId, String inputPath, String preset) {
        this.jobId = UUID.randomUUID().toString();
        this.videoId = videoId;
        this.inputPath = inputPath;
        this.preset = preset;
    }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getInputPath() { return inputPath; }
    public void setInputPath(String inputPath) { this.inputPath = inputPath; }

    public String getPreset() { return preset; }
    public void setPreset(String preset) { this.preset = preset; }
}