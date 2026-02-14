package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.ftn.isa.backend.domain.model.UserActivity;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    @Query("SELECT ua FROM UserActivity ua WHERE ua.user.id = :userId")
    Optional<UserActivity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ua) FROM UserActivity ua WHERE ua.lastActivity > :time")
    long countActiveUsers(@Param("time") LocalDateTime time);

    default long countUsersActiveInLast24Hours() {
        return countActiveUsers(LocalDateTime.now().minusHours(24));
    }
}