package rs.ac.ftn.isa.backend.controller;

import java.io.IOException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.ftn.isa.backend.domain.model.etlPipeline.VideoView;
import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.repository.etlPipeline.VideoViewRepository;
import rs.ac.ftn.isa.backend.service.PopularVideosEtlService;
import rs.ac.ftn.isa.backend.service.VideoLikeService;
import rs.ac.ftn.isa.backend.service.VideoPostService;
import rs.ac.ftn.isa.backend.dto.VideoPostResponse;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class VideoPostController {

    @Autowired
    private VideoPostService videoPostService;

    @Autowired
    private VideoLikeService videoLikeService;

    @Autowired
    private VideoViewRepository videoViewRepository;

    @Autowired
    private PopularVideosEtlService popularVideosEtlService;

    @PostMapping(
            value = "/videos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createVideo(
            @RequestPart("data") VideoPostRequest request,
            @RequestPart("video") MultipartFile video,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            Principal principal
    ) {
        try {
            videoPostService.create(
                    request,
                    video,
                    thumbnail,
                    principal.getName()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping(
            value = "/videos/{id}/stream",
            produces = "video/mp4"
    )
    public ResponseEntity<byte[]> streamVideo(@PathVariable Long id) {
        try {
            byte[] videoBytes = videoPostService.getVideo(id);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .body(videoBytes);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/videos")
    //@PreAuthorize("isAuthenticated()")
    public List<VideoPostResponse> getAllVideos() {
        return videoPostService.findAllResponses();
    }

    @GetMapping("/videos/trending")
    public ResponseEntity<List<VideoPostResponse>> getTrendingVideos() {
        return ResponseEntity.ok(
                videoPostService.findResponsesByIds(
                        popularVideosEtlService.getLatestTop3VideoIds()
                )
        );
    }

    @GetMapping(value = "/videos/{id}/thumbnail", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getThumbnail(@PathVariable("id") Long videoId) {
        try {
            byte[] thumbnail = videoPostService.getThumbnail(videoId);
            return ResponseEntity.ok(thumbnail);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/videos/{id}")
    public ResponseEntity<VideoPostResponse> getVideoById(@PathVariable Long id, Principal principal) {
        videoPostService.incrementViews(id);
        videoViewRepository.save(new VideoView(id, new Timestamp(System.currentTimeMillis())));

        String viewerEmail = (principal != null) ? principal.getName() : null;

        return videoPostService.findResponseById(id, viewerEmail)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/videos/{id}/likes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> likeVideo(@PathVariable Long id, Principal principal) {
        videoLikeService.like(id, principal.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/videos/{id}/likes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> unlikeVideo(@PathVariable Long id, Principal principal) {
        videoLikeService.unlike(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}
