package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import rs.ac.ftn.isa.backend.model.VideoComment;

import java.sql.Timestamp;

public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {

    Page<VideoComment> findByVideoPost_IdOrderByCreatedAtDesc(Long videoPostId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM VideoComment c WHERE c.user.id = :userId AND c.createdAt >= :since")
    long countUserCommentsSince(@Param("userId") Long userId, @Param("since") Timestamp since);
}