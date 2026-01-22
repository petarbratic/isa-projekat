package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

import rs.ac.ftn.isa.backend.domain.enums.WatchPartyStatus;

@Entity
@Table(name = "WATCH_PARTY_ROOMS")
public class WatchPartyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @Column(name = "join_code", nullable = false, unique = true)
    private String joinCode;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @ManyToOne
    @JoinColumn(name = "active_video_id")
    private VideoPost activeVideo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WatchPartyStatus status = WatchPartyStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // getters & setters
}