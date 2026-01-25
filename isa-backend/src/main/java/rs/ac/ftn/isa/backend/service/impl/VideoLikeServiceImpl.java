package rs.ac.ftn.isa.backend.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.domain.model.VideoLike;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.VideoLikeRepository;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.UserService;
import rs.ac.ftn.isa.backend.service.VideoLikeService;

@Service
public class VideoLikeServiceImpl implements VideoLikeService {

    @Autowired
    private VideoLikeRepository videoLikeRepository;

    @Autowired
    private VideoPostRepository videoPostRepository;

    @Autowired
    private UserService userService;

    @Override
    public long getLikesCount(Long videoId) {
        return videoLikeRepository.countByVideo_Id(videoId);
    }

    @Override
    public boolean isLikedByUser(Long videoId, Long userId) {
        return videoLikeRepository.existsByVideo_IdAndUser_Id(videoId, userId);
    }

    @Override
    @Transactional
    public void like(Long videoId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        System.out.println("principal.getName() = " + user.getFirstName());
        if (user == null) throw new IllegalArgumentException("User not found");

        VideoPost video = videoPostRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        if (videoLikeRepository.existsByVideo_IdAndUser_Id(videoId, user.getId())) return;

        VideoLike like = new VideoLike();
        like.setUser(user);
        like.setVideo(video);
        videoLikeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlike(Long videoId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        if (user == null) throw new IllegalArgumentException("User not found");

        videoLikeRepository.deleteByVideo_IdAndUser_Id(videoId, user.getId());
    }
}
