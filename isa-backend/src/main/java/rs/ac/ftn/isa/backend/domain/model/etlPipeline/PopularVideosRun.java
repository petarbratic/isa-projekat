package rs.ac.ftn.isa.backend.domain.model.etlPipeline;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "POPULAR_VIDEOS_RUN")
public class PopularVideosRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "executed_at", nullable = false)
    private Timestamp executedAt;

    protected PopularVideosRun() {}

    public PopularVideosRun(Timestamp executedAt) {
        this.executedAt = executedAt;
    }

    public Long getId() { return id; }
    public Timestamp getExecutedAt() { return executedAt; }
}
