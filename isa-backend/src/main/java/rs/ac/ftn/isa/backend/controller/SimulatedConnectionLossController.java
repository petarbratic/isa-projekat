package rs.ac.ftn.isa.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.ftn.isa.backend.cluster.SimulatedConnectionLossHolder;

/**
 * Simulacija parcijalnog gubitka konekcije – jedna replika "gubi" bazu.
 * Pozovi na ovoj replici → njena /actuator/health vraća DOWN → load balancer šalje samo na drugu.
 */
@RestController
@RequestMapping("/api/admin")
public class SimulatedConnectionLossController {

    @GetMapping("/simulate-connection-loss")
    @PostMapping("/simulate-connection-loss")
    public ResponseEntity<String> simulateLoss() {
        SimulatedConnectionLossHolder.setSimulatedDown(true);
        return ResponseEntity.ok("Simuliran gubitak konekcije. Ova replika će sada vraćati health=DOWN.");
    }

    @GetMapping("/simulate-connection-restore")
    @PostMapping("/simulate-connection-restore")
    public ResponseEntity<String> simulateRestore() {
        SimulatedConnectionLossHolder.setSimulatedDown(false);
        return ResponseEntity.ok("Konekcija vraćena. Ova replika ponovo vraća health=UP.");
    }
}
