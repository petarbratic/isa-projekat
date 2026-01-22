package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.domain.model.VideoLike;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    long countByVideo_Id(Long videoId);
    boolean existsByVideo_IdAndUser_Id(Long videoId, Long userId);
    void deleteByVideo_IdAndUser_Id(Long videoId, Long userId);
}
