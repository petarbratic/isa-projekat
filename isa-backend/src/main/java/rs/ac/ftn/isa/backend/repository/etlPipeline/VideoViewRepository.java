package rs.ac.ftn.isa.backend.repository.etlPipeline;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import rs.ac.ftn.isa.backend.domain.model.etlPipeline.VideoView;
import rs.ac.ftn.isa.backend.repository.etlPipeline.projection.DailyVideoViewsRow;

import java.sql.Timestamp;
import java.util.List;

public interface VideoViewRepository extends JpaRepository<VideoView, Long> {

    @Query(value = """
        SELECT
            vv.video_id AS videoId,
            DATE(vv.viewed_at) AS day,
            COUNT(*) AS views
        FROM video_views vv
        WHERE vv.viewed_at >= :from
          AND vv.viewed_at <  :to
        GROUP BY vv.video_id, DATE(vv.viewed_at)
        """, nativeQuery = true)
    List<DailyVideoViewsRow> findDailyViews(
            @Param("from") Timestamp from,
            @Param("to") Timestamp to
    );

}
