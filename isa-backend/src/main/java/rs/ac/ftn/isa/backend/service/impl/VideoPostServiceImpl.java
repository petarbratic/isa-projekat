package rs.ac.ftn.isa.backend.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.model.User;
import rs.ac.ftn.isa.backend.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.UserRepository;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.VideoPostService;

@Service
public class VideoPostServiceImpl implements VideoPostService {

    @Autowired
    private VideoPostRepository videoPostRepository;

    @Autowired
    private UserRepository userRepository;

    private final String VIDEO_DIR = "uploads/videos/";
    private final String THUMB_DIR = "uploads/thumbnails/";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(VideoPostRequest dto,
                       MultipartFile video,
                       MultipartFile thumbnail,
                       String userEmail) throws IOException {

        if (!video.getContentType().equals("video/mp4")) {
            throw new IllegalArgumentException("Only MP4 videos are allowed");
        }

        if (video.getSize() > 200 * 1024 * 1024) { // 200MB limit
            throw new IllegalArgumentException("Video file is too large");
        }

        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        VideoPost post = new VideoPost();
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setTags(dto.getTags());
        post.setLocation(dto.getLocation());
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setOwner(user);

        videoPostRepository.save(post);

        Path videoPath = Paths.get(VIDEO_DIR + post.getId() + ".mp4");
        Path thumbPath = Paths.get(THUMB_DIR + post.getId() + ".png");

        try {
            Files.createDirectories(Paths.get(VIDEO_DIR));
            Files.createDirectories(Paths.get(THUMB_DIR));

            Files.copy(video.getInputStream(), videoPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(thumbnail.getInputStream(), thumbPath, StandardCopyOption.REPLACE_EXISTING);

            post.setVideoPath(videoPath.toString());
            post.setThumbnailPath(thumbPath.toString());

            videoPostRepository.save(post);

        } catch (Exception e) {
            try {
                if (Files.exists(videoPath)) Files.delete(videoPath);
                if (Files.exists(thumbPath)) Files.delete(thumbPath);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            throw new IOException("Failed to save video or thumbnail", e);
        }
    }

    @Override
    public byte[] getVideo(Long videoId) throws IOException {
        VideoPost post = videoPostRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        return Files.readAllBytes(Path.of(post.getVideoPath()));
    }

    @Override
    public List<VideoPost> findAll() {
        return videoPostRepository.findAll();
    }


    @Cacheable(value = "thumbnails", key = "#videoId")
    public byte[] getThumbnail(Long videoId) throws IOException {
        VideoPost post = videoPostRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));
        return Files.readAllBytes(Path.of(post.getThumbnailPath()));
    }
}
