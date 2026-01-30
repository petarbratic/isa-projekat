package rs.ac.ftn.isa.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.ac.ftn.isa.backend.domain.crdt.GCounterState;
import rs.ac.ftn.isa.backend.service.ViewCounterService;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile({"replica1", "replica2"})
@RestController
@RequestMapping("/internal/crdt")
public class ViewCounterSyncController {

    private final ViewCounterService service;

    public ViewCounterSyncController(ViewCounterService service) {
        this.service = service;
    }

    @PostMapping("/gcounter/push")
    public ResponseEntity<Void> push(@RequestBody List<GCounterState> states) {
        service.mergeIncoming(states);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/gcounter/state")
    public ResponseEntity<List<GCounterState>> state() {
        return ResponseEntity.ok(service.exportStates());
    }
}