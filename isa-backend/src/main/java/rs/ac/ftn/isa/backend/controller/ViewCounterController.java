package rs.ac.ftn.isa.backend.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.isa.backend.service.ViewCounterService;
import org.springframework.context.annotation.Profile;

@Profile({"replica1", "replica2"})
@RestController
@RequestMapping("/counter")
public class ViewCounterController {

    private final ViewCounterService service;

    public ViewCounterController(ViewCounterService service) {
        this.service = service;
    }

    @PostMapping("/{videoId}/inc")
    public long inc(@PathVariable long videoId) {
        return service.incrementLocal(videoId);
    }

    @GetMapping("/{videoId}")
    public long get(@PathVariable long videoId) {
        return service.getTotal(videoId);
    }
}