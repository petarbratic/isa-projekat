package rs.ac.ftn.isa.backend.service;

public interface VideoLikeService {
    void like(Long videoId, String userEmail);
    void unlike(Long videoId, String userEmail);

    long getLikesCount(Long videoId);
    boolean isLikedByUser(Long videoId, Long userId);
}