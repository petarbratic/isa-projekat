package rs.ac.ftn.isa.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import rs.ac.ftn.isa.backend.dto.CommentResponse;
import rs.ac.ftn.isa.backend.dto.CreateCommentRequest;
import rs.ac.ftn.isa.backend.service.VideoCommentService;

import java.security.Principal;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class VideoCommentController {

    @Autowired
    private VideoCommentService commentService;

    @GetMapping("/videos/{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable("id") Long videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.getComments(videoId, PageRequest.of(page, size)));
    }

    @PostMapping("/videos/{id}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable("id") Long videoId,
            @RequestBody CreateCommentRequest req,
            Principal principal
    ) {
        CommentResponse created = commentService.addComment(videoId, principal.getName(), req.getText());
        return ResponseEntity.ok(created);
    }
}
