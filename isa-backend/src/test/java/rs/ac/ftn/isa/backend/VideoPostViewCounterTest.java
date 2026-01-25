package rs.ac.ftn.isa.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;
import rs.ac.ftn.isa.backend.service.VideoPostService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.cache.use_second_level_cache=false",
        "spring.jpa.properties.hibernate.cache.use_query_cache=false"
})

class VideoPostViewCounterTest {

    @Autowired
    private VideoPostRepository videoPostRepository;

    @Autowired
    private VideoPostService videoPostService;

    @Test
    void concurrentViewIncrement_shouldBeConsistent() throws Exception {

        VideoPost video = new VideoPost();
        video.setTitle("Test video");
        video.setDescription("desc");

        video = videoPostRepository.save(video);
        final Long videoId = video.getId();

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                videoPostService.incrementViews(videoId);
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        VideoPost updated = videoPostRepository.findById(videoId).orElseThrow();

        assertEquals(threads, updated.getViews());
    }
}


