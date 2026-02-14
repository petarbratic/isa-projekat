package rs.ac.ftn.isa.backend.service.impl;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.service.ThumbnailCompressionService;
import net.coobird.thumbnailator.geometry.Positions;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

@Service
public class ThumbnailCompressionServiceImpl implements ThumbnailCompressionService {

    private static final String COMPRESSED_DIR = "uploads/thumbnails/compressed/";

    @Override
    public boolean compressThumbnail(VideoPost post) throws IOException {
        if (post.getThumbnailPath() == null) return false;
        if (post.getThumbnailCompressedPath() != null) return false;

        Path original = Paths.get(post.getThumbnailPath());
        if (!Files.exists(original)) return false;

        Files.createDirectories(Paths.get(COMPRESSED_DIR));

        Path out = Paths.get(COMPRESSED_DIR + post.getId() + ".jpg");

        int targetW = 320;
        int targetH = 180;
        double targetRatio = (double) targetW / targetH;

        BufferedImage img = ImageIO.read(original.toFile());
        int w = img.getWidth();
        int h = img.getHeight();
        double srcRatio = (double) w / h;

        int cropW = w, cropH = h;
        if (srcRatio > targetRatio) {
            cropW = (int) (h * targetRatio);
        } else {
            cropH = (int) (w / targetRatio);
        }

        Thumbnails.of(original.toFile())
                .sourceRegion(Positions.CENTER, cropW, cropH)
                .size(targetW, targetH)
                .keepAspectRatio(false)
                .outputFormat("jpg")
                .outputQuality(0.75)
                .toFile(out.toFile());

        post.setThumbnailCompressedPath(out.toString());
        post.setThumbnailCompressedAt(new Timestamp(System.currentTimeMillis()));
        return true;
    }
}
