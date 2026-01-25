package rs.ac.ftn.isa.backend.controller;

import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.isa.backend.service.ViewCounterService;

@RestController
@RequestMapping("/counter")
public class ViewCounterController {

    private final ViewCounterService service;

    public ViewCounterController(ViewCounterService service) {
        this.service = service;
    }

    @PostMapping("/inc")
    public long inc() {
        return service.incrementLocal();
    }

    @GetMapping
    public long get() {
        return service.getLocal();
    }
}
