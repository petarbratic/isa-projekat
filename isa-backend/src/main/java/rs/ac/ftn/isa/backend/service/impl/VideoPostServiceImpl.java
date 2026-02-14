package rs.ac.ftn.isa.backend.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.UserRepository;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.VideoLikeService;
import rs.ac.ftn.isa.backend.service.VideoPostService;
import rs.ac.ftn.isa.backend.dto.UploadEvent;
import rs.ac.ftn.isa.backend.dto.VideoPostResponse;
import rs.ac.ftn.isa.backend.service.transcoding.TranscodingProducer;
import rs.ac.ftn.isa.backend.service.uploadevent.UploadEventProducer;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Service
public class VideoPostServiceImpl implements VideoPostService {

    @Autowired
    private VideoPostRepository videoPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoLikeService videoLikeService;

    private final String VIDEO_DIR = "uploads/videos/";
    private final String THUMB_DIR = "uploads/thumbnails/";

    @Autowired
    private TranscodingProducer transcodingProducer;

    @Autowired
    private UploadEventProducer uploadEventProducer;

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

        Timestamp scheduledAt = dto.getScheduledAt();
        if (scheduledAt != null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (scheduledAt.before(now)) {
                throw new IllegalArgumentException("scheduledAt must be in the future");
            }
        }

        VideoPost post = new VideoPost();
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setTags(dto.getTags());
        post.setLocation(dto.getLocation());
        post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        post.setOwner(user);
        post.setScheduledAt(scheduledAt);

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


            boolean isScheduled = (post.getScheduledAt() != null);
            transcodingProducer.publish(
                    new rs.ac.ftn.isa.backend.dto.TranscodeJobMessage(
                            post.getId(),
                            post.getVideoPath(),
                            "MP4_720P",
                            isScheduled
                    )
            );

            // Publish upload event (JSON + Protobuf) for new video
            UploadEvent uploadEvent = new UploadEvent(
                    post.getId(),
                    post.getTitle(),
                    video.getSize(),
                    user.getId() != null ? user.getId().toString() : "",
                    user.getUsername() != null ? user.getUsername() : user.getEmail(),
                    post.getCreatedAt() != null ? post.getCreatedAt().toInstant().toString() : ""
            );
            uploadEventProducer.sendJson(uploadEvent);
            uploadEventProducer.sendProtobuf(uploadEvent);
            uploadEventProducer.sendToInspectQueue(uploadEvent);
            uploadEventProducer.sendToInspectProtobufQueue(uploadEvent);

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
        return videoPostRepository.findAllByOrderByCreatedAtDesc();
    }

    @Cacheable(value = "thumbnails", key = "#videoId")
    public byte[] getThumbnail(Long videoId) throws IOException {
        VideoPost post = videoPostRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found"));

        String path = (post.getThumbnailCompressedPath() != null)
                ? post.getThumbnailCompressedPath()
                : post.getThumbnailPath();

        return Files.readAllBytes(Path.of(path));
    }

    public Optional<VideoPost> findById(Long id) {
        return videoPostRepository.findById(id);
    }

    @Override
    public List<VideoPost> findByOwnerId(Long ownerId) {
        return videoPostRepository.findByOwner_IdOrderByCreatedAtDesc(ownerId);
    }

    private VideoPostResponse toResponse(VideoPost post) {
        VideoPostResponse dto = new VideoPostResponse();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setTags(post.getTags());
        dto.setLocation(post.getLocation());
        dto.setCreatedAt(post.getCreatedAt());

        if (post.getOwner() != null) {
            dto.setOwnerId(post.getOwner().getId());

            String first = post.getOwner().getFirstName() != null ? post.getOwner().getFirstName() : "";
            String last = post.getOwner().getLastName() != null ? post.getOwner().getLastName() : "";
            String fullName = (first + " " + last).trim();

            if (fullName.isBlank()) {
                fullName = post.getOwner().getEmail();
            }

            dto.setOwnerFullName(fullName);
        }
        dto.setViews(post.getViews());
        return dto;
    }

    public List<VideoPostResponse> findAllResponses() {
        return videoPostRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VideoPostResponse> findResponsesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        // 1) Fetch iz baze
        List<VideoPost> posts = videoPostRepository.findByIdIn(ids);

        // 2) Zadrži redosled kao u ulaznoj listi ids
        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            order.put(ids.get(i), i);
        }

        return posts.stream()
                .sorted(Comparator.comparingInt(p -> order.getOrDefault(p.getId(), Integer.MAX_VALUE)))
                .map(this::toResponse)
                .toList();
    }

    public Optional<VideoPostResponse> findResponseById(Long id) {
        return videoPostRepository.findById(id).map(this::toResponse);
    }

    @Override
    @Transactional
    public void incrementViews(Long videoId) {
        videoPostRepository.incrementViews(videoId);
    }

    private VideoPostResponse toResponse(VideoPost post, String viewerEmail) {
        VideoPostResponse dto = toResponse(post); // koristi postojeće mapiranje

        long likesCount = videoLikeService.getLikesCount(post.getId());
        dto.setLikesCount(likesCount);

        boolean likedByMe = false;
        if (viewerEmail != null) {
            User viewer = userRepository.findByEmail(viewerEmail);
            if (viewer != null) {
                likedByMe = videoLikeService.isLikedByUser(post.getId(), viewer.getId());
            }
        }
        dto.setLikedByMe(likedByMe);

        return dto;
    }

    public Optional<VideoPostResponse> findResponseById(Long id, String viewerEmail) {
        return videoPostRepository.findById(id).map(p -> toResponse(p, viewerEmail));
    }


    @Override
    public Optional<rs.ac.ftn.isa.backend.dto.VideoPremiereResponse> getPremiere(Long videoId) {
        VideoPost post = videoPostRepository.findById(videoId).orElse(null);
        if (post == null) return Optional.empty();

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        rs.ac.ftn.isa.backend.dto.VideoPremiereResponse resp = new rs.ac.ftn.isa.backend.dto.VideoPremiereResponse();
        resp.setServerNow(now);
        resp.setScheduledAt(post.getScheduledAt());

        // Zakazano, ali nije počelo
        if (post.getScheduledAt() != null && now.before(post.getScheduledAt())) {
            resp.setAvailable(false);
            resp.setMode("WAIT");
            resp.setUrl(null);
            resp.setOffsetSeconds(0);
            return Optional.of(resp);
        }

        // Zakazano i počelo -> HLS + offset
        if (post.getScheduledAt() != null) {
            long offset = (now.getTime() - post.getScheduledAt().getTime()) / 1000;
            resp.setAvailable(true);
            resp.setMode("HLS");
            resp.setOffsetSeconds(Math.max(0, offset));
            String hls = post.getHlsPlaylistPath(); // npr "/media/transcoded/1/hls/index.m3u8"
            resp.setUrl(hls == null ? null : baseUrl + hls);
            return Optional.of(resp);
        }

        // Nije zakazano -> MP4 preko /media
        resp.setAvailable(true);
        resp.setMode("MP4");
        resp.setOffsetSeconds(0);
        String mp4 = toMediaUrl(post.getVideoPath()); // npr "/media/videos/4.mp4"
        resp.setUrl(mp4 == null ? null : baseUrl + mp4);
        return Optional.of(resp);
    }

    private String toMediaUrl(String diskPath) {
        if (diskPath == null) return null;

        String p = diskPath.replace("\\", "/").trim();

        // ukloni višestruke /
        while (p.contains("//")) p = p.replace("//", "/");

        // ako već vraća URL putanju
        if (p.startsWith("/media/")) return p;

        // ako je relativno na uploads/
        if (p.startsWith("uploads/")) {
            String rest = p.substring("uploads/".length());
            // ukloni vodeći /
            while (rest.startsWith("/")) rest = rest.substring(1);
            return "/media/" + rest;
        }

        // general case: ukloni vodeći /
        while (p.startsWith("/")) p = p.substring(1);
        return "/media/" + p;
    }


}
