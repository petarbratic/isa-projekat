package rs.ac.ftn.isa.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rs.ac.ftn.isa.backend.dto.CommentResponse;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.domain.model.VideoComment;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.UserRepository;
import rs.ac.ftn.isa.backend.repository.VideoCommentRepository;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.VideoCommentService;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import java.sql.Timestamp;

@Service
public class VideoCommentServiceImpl implements VideoCommentService {

    private static final int MAX_COMMENTS_PER_HOUR = 60;

    @Autowired private VideoCommentRepository commentRepository;
    @Autowired private VideoPostRepository videoPostRepository;
    @Autowired private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long videoId, Pageable pageable) {
        return commentRepository
                .findByVideoPost_IdOrderByCreatedAtDesc(videoId, pageable)
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getUser().getId(),
                        c.getUser().getEmail(),
                        c.getUser().getFirstName() + " " + c.getUser().getLastName(),
                        c.getText(),
                        c.getCreatedAt()
                ));
    }

    @Override
    @Transactional
    public CommentResponse addComment(Long videoId, String userEmail, String text) {

        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text is required.");
        }

        User user = userRepository.findByEmail(userEmail);
        if (user == null) throw new IllegalArgumentException("User not found");

        VideoPost video = videoPostRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        Timestamp since = new Timestamp(System.currentTimeMillis() - 60L * 60L * 1000L);
        long countLastHour = commentRepository.countUserCommentsSince(user.getId(), since);

        if (countLastHour >= MAX_COMMENTS_PER_HOUR) {
            throw new ResponseStatusException(TOO_MANY_REQUESTS, "Limit is 60 comments per hour.");
        }

        VideoComment comment = new VideoComment();
        comment.setText(text.trim());
        comment.setUser(user);
        comment.setVideoPost(video);

        commentRepository.save(comment);

        return new CommentResponse(
                comment.getId(),
                user.getId(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                comment.getText(),
                comment.getCreatedAt()
        );
    }
}