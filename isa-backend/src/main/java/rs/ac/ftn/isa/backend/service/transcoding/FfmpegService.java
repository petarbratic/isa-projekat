package rs.ac.ftn.isa.backend.service.transcoding;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        Process p = pb.start();

        // PROCITAJ LOG da vidiš grešku
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
