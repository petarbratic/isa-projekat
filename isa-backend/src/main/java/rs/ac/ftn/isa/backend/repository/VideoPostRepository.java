package rs.ac.ftn.isa.backend.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.model.VideoPost;


public interface VideoPostRepository extends JpaRepository<VideoPost, Long> {
}
