package rs.ac.ftn.isa.backend.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.isa.backend.dto.UploadEventBenchmarkResult;
import rs.ac.ftn.isa.backend.service.uploadevent.UploadEventBenchmarkService;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UploadEventBenchmarkController {

    private final UploadEventBenchmarkService benchmarkService;

    public UploadEventBenchmarkController(UploadEventBenchmarkService benchmarkService) {
        this.benchmarkService = benchmarkService;
    }

    /**
     * Poređenje JSON vs Protobuf: prosečno vreme serijalizacije, deserijalizacije i veličina poruke.
     * Koristi najmanje 50 poruka. Opcioni query param: count (podrazumevano 50).
     */
    @GetMapping("/upload-event/benchmark")
    public UploadEventBenchmarkResult runBenchmark(
            @RequestParam(value = "count", defaultValue = "50") int count) {
        return benchmarkService.runBenchmark(count);
    }
}
