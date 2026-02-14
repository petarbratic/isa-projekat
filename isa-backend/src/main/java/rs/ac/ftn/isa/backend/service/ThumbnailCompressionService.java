package rs.ac.ftn.isa.backend.service;

import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import java.io.IOException;

public interface ThumbnailCompressionService {
    boolean compressThumbnail(VideoPost post) throws IOException;
}