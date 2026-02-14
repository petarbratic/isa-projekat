package rs.ac.ftn.isa.backend.service.transcoding;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class FfmpegService {

    public void transcodeTo720pMp4(String inputPath, String outputPath) throws IOException, InterruptedException {
        new File(outputPath).getParentFile().mkdirs();

        List<String> cmd = List.of(
                "ffmpeg",
                "-y",
                "-hide_banner",
                "-loglevel", "error",
                "-i", inputPath,

                "-vf", "scale=-2:720",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-preset", "veryfast",
                "-crf", "23",

                "-c:a", "aac",
                "-b:a", "128k",
                "-ac", "2",
                "-ar", "44100",

                "-movflags", "+faststart",

                outputPath
        );

        run(cmd);
    }


    public void transcodeToHls720p(String inputPath, String outputDir) throws IOException, InterruptedException {
        Files.createDirectories(Path.of(outputDir));
        String playlistPath = Path.of(outputDir, "index.m3u8").toString();
        String segmentPattern = Path.of(outputDir, "seg_%05d.ts").toString();

        List<String> cmd = List.of(
                "ffmpeg",
                "-y",
                "-hide_banner",
                "-loglevel", "error",
                "-i", inputPath,

                "-vf", "scale=-2:720",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                "-preset", "veryfast",
                "-crf", "23",

                "-c:a", "aac",
                "-b:a", "128k",
                "-ac", "2",
                "-ar", "44100",

                // HLS deo:
                "-hls_time", "6",                 // duzina segmenta (sek)
                "-hls_playlist_type", "vod",      // VOD playlist (nema "live" kliznog prozora)
                "-hls_segment_filename", segmentPattern,

                playlistPath
        );

        run(cmd);
    }

    private void run(List<String> cmd) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[FFMPEG] " + line);
            }
        }

        int exit = p.waitFor();
        if (exit != 0) {
            throw new RuntimeException("FFmpeg failed with exit code " + exit);
        }
    }
}