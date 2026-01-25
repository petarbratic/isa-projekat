package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.QueryHints;

import rs.ac.ftn.isa.backend.domain.model.VideoComment;

import java.sql.Timestamp;

public interface VideoCommentRepository extends JpaRepository<VideoComment, Long> {

    @QueryHints({
            @QueryHint(name = "org.hibernate.cacheable", value = "true"),
            @QueryHint(name = "org.hibernate.cacheRegion", value = "videoCommentsByVideo")
    })
    Page<VideoComment> findByVideoPost_IdOrderByCreatedAtDesc(Long videoPostId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM VideoComment c WHERE c.user.id = :userId AND c.createdAt >= :since")
    long countUserCommentsSince(@Param("userId") Long userId, @Param("since") Timestamp since);
}