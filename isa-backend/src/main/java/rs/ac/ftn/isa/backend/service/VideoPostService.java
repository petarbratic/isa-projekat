package rs.ac.ftn.isa.backend.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import rs.ac.ftn.isa.backend.dto.VideoPostRequest;
import rs.ac.ftn.isa.backend.model.VideoPost;

public interface VideoPostService {

    void create(VideoPostRequest dto,
                MultipartFile video,
                MultipartFile thumbnail,
                String userEmail) throws IOException;

    List<VideoPost> findAll();
    byte[] getVideo(Long videoId) throws IOException;

    byte[] getThumbnail(Long videoId) throws IOException;
}
