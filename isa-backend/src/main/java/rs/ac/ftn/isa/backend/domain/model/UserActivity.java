package rs.ac.ftn.isa.backend.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity")
public class UserActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Jedan korisnik ima jednu aktivnost (poslednja aktivnost)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;

    public UserActivity() {
    }

    public UserActivity(User user, LocalDateTime lastActivity) {
        this.user = user;
        this.lastActivity = lastActivity;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}