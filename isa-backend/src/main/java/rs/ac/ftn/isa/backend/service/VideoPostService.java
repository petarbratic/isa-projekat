package rs.ac.ftn.isa.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.dto.VideoPostResponse;
import rs.ac.ftn.isa.backend.model.VideoPost;

public interface VideoPostService {

    void create(VideoPostRequest dto,
                MultipartFile video,
                MultipartFile thumbnail,
                String userEmail) throws IOException;

    List<VideoPost> findAll();
    byte[] getVideo(Long videoId) throws IOException;

    byte[] getThumbnail(Long videoId) throws IOException;

    public Optional<VideoPost> findById(Long id);

    List<VideoPost> findByOwnerId(Long ownerId);

    List<VideoPostResponse> findAllResponses();
    Optional<VideoPostResponse> findResponseById(Long id, String viewerEmail);
    void incrementViews(Long videoId);
}
