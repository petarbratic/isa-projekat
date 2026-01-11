package rs.ac.ftn.isa.backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

import rs.ac.ftn.isa.backend.model.enums.WatchPartyRole;

@Entity
@Table(
        name = "WATCH_PARTY_MEMBERS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"})
)
public class WatchPartyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private WatchPartyRoom room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private WatchPartyRole role = WatchPartyRole.MEMBER;

    @Column(name = "joined_at", nullable = false)
    private Timestamp joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = new Timestamp(System.currentTimeMillis());
    }

    // getters & setters
}