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
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "C:/projekti/uploads/";

        // kreiraj folder ako ne postoji
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path path = uploadPath.resolve(file.getOriginalFilename());
        System.out.println("Čuvam fajl u: " + path.toAbsolutePath());

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        String fileUrl = "http://localhost:8081/uploads/" + file.getOriginalFilename();
        return ResponseEntity.ok(fileUrl);
    }



    @GetMapping("/videos")
    public List<VideoPost> getAllVideos() {
        return videoPostService.findAll();
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
}
