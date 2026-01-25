package rs.ac.ftn.isa.backend.repository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import java.util.List;

public interface VideoPostRepository extends JpaRepository<VideoPost, Long> {
    List<VideoPost> findAllByOrderByCreatedAtDesc();
    List<VideoPost> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
    @Modifying
    @Transactional
    @Query("UPDATE VideoPost v SET v.views = v.views + 1 WHERE v.id = :videoId")
    void incrementViews(Long videoId);
}
