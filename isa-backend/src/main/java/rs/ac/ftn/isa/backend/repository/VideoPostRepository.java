package rs.ac.ftn.isa.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.model.VideoPost;
import java.util.List;

public interface VideoPostRepository extends JpaRepository<VideoPost, Long> {
    List<VideoPost> findAllByOrderByCreatedAtDesc();
    List<VideoPost> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
}
