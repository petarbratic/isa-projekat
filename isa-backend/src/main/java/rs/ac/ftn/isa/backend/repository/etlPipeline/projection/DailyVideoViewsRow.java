package rs.ac.ftn.isa.backend.repository.etlPipeline.projection;

public interface DailyVideoViewsRow {
    Long getVideoId();
    java.sql.Date getDay();
    Long getViews();
}