package rs.ac.ftn.isa.backend.service.transcoding;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.config.TranscodingMqConfig;
import rs.ac.ftn.isa.backend.domain.model.TranscodingJob;
import rs.ac.ftn.isa.backend.dto.TranscodeJobMessage;
import rs.ac.ftn.isa.backend.repository.TranscodingJobRepository;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;
import rs.ac.ftn.isa.backend.repository.VideoPostRepository;

import java.io.IOException;
import java.util.Optional;

@Service
public class TranscodingConsumer {

    private final TranscodingJobRepository jobRepo;
    private final FfmpegService ffmpegService;
    private final VideoPostRepository videoPostRepo;

    public TranscodingConsumer(TranscodingJobRepository jobRepo, VideoPostRepository videoPostRepo, FfmpegService ffmpegService) {
        this.jobRepo = jobRepo;
        this.ffmpegService = ffmpegService;
        this.videoPostRepo = videoPostRepo;
    }

    @RabbitListener(queues = TranscodingMqConfig.TRANSCODE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handle(TranscodeJobMessage msg, Channel channel,
                       org.springframework.amqp.core.Message amqpMsg) throws IOException {

        long tag = amqpMsg.getMessageProperties().getDeliveryTag();

        try {
            TranscodingJob job = ensureJobExists(msg);

            if (job.getStatus() == TranscodingJob.Status.DONE) {
                channel.basicAck(tag, false);
                return;
            }

            markProcessing(job.getId());

            // preset sada samo 720p mp4 (predefinisano)
            String mp4Out = "uploads/transcoded/" + msg.getVideoId() + "_720p.mp4";
            ffmpegService.transcodeTo720pMp4(msg.getInputPath(), mp4Out);

            String hlsPlaylistUrl = null;

            if (msg.getScheduled()) {
                // disk folder (bez duplog "hls")
                String hlsDir = "uploads/transcoded/" + msg.getVideoId() + "/hls";
                ffmpegService.transcodeToHls720p(mp4Out, hlsDir);

                // URL koji FE može da koristi (mapiraš /media/** -> uploads/**)
                hlsPlaylistUrl = "/media/transcoded/" + msg.getVideoId() + "/hls/index.m3u8";
            }

            markDone(job.getId(), mp4Out, hlsPlaylistUrl);

            channel.basicAck(tag, false);

        } catch (Exception ex) {
            ex.printStackTrace();

            try {
                TranscodingJob existing = jobRepo.findByJobId(msg.getJobId()).orElse(null);
                if (existing != null) {
                    markFailed(existing.getId(), ex.toString());
                }
            } catch (Exception ignored) { }

            channel.basicNack(tag, false, false);
        }
    }

    @Transactional
    protected TranscodingJob ensureJobExists(TranscodeJobMessage msg) {
        Optional<TranscodingJob> existing = jobRepo.findByJobId(msg.getJobId());
        if (existing.isPresent()) return existing.get();

        TranscodingJob job = new TranscodingJob();
        job.setJobId(msg.getJobId());
        job.setVideoId(msg.getVideoId());
        job.setPreset(msg.getPreset());
        job.setStatus(TranscodingJob.Status.PENDING);
        return jobRepo.save(job);
    }

    @Transactional
    protected void markProcessing(Long jobDbId) {
        TranscodingJob job = jobRepo.findById(jobDbId).orElseThrow();
        job.setStatus(TranscodingJob.Status.PROCESSING);
        jobRepo.save(job);
    }

    @Transactional
    protected void markDone(Long jobDbId, String outPath, String hlsPlaylistUrl) {
        TranscodingJob job = jobRepo.findById(jobDbId).orElseThrow();
        job.setStatus(TranscodingJob.Status.DONE);
        job.setOutputPath(outPath);
        jobRepo.save(job);

        VideoPost post = videoPostRepo.findById(job.getVideoId())
                .orElseThrow(() -> new IllegalStateException("VideoPost not found: " + job.getVideoId()));

        // ako je zakazano, upiši HLS playlist url
        if (hlsPlaylistUrl != null) {
            post.setHlsPlaylistPath(hlsPlaylistUrl);
        }

        videoPostRepo.save(post);
    }

    @Transactional
    protected void markFailed(Long jobDbId, String error) {
        TranscodingJob job = jobRepo.findById(jobDbId).orElseThrow();
        job.setStatus(TranscodingJob.Status.FAILED);
        job.setErrorMessage(error);
        jobRepo.save(job);
    }
}