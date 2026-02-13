package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "TRANSCODING_JOBS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"job_id"})
})
public class TranscodingJob {

    public enum Status { PENDING, PROCESSING, DONE, FAILED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="job_id", nullable=false, unique=true)
    private String jobId;

    @Column(name="video_id", nullable=false)
    private Long videoId;

    @Column(name="preset", nullable=false)
    private String preset;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private Status status;

    @Column(name="output_path")
    private String outputPath;

    @Column(name="error_message", length = 2000)
    private String errorMessage;

    @Column(name="created_at", nullable=false)
    private Timestamp createdAt;

    @Column(name="updated_at", nullable=false)
    private Timestamp updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Long getId() { return id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getPreset() { return preset; }
    public void setPreset(String preset) { this.preset = preset; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}