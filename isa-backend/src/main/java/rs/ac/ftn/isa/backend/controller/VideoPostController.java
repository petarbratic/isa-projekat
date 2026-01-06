package rs.ac.ftn.isa.backend.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.model.VideoPost;
import rs.ac.ftn.isa.backend.service.VideoPostService;
import rs.ac.ftn.isa.backend.dto.VideoPostResponse;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class VideoPostController {

    @Autowired
    private VideoPostService videoPostService;

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
    //@PreAuthorize("isAuthenticated()")
    public ResponseEntity<VideoPostResponse> getVideoById(@PathVariable Long id) {
        videoPostService.incrementViews(id);
        return videoPostService.findResponseById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
