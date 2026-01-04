package rs.ac.ftn.isa.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rs.ac.ftn.isa.backend.dto.CommentResponse;

public interface VideoCommentService {
    Page<CommentResponse> getComments(Long videoId, Pageable pageable);
    CommentResponse addComment(Long videoId, String userEmail, String text);
}