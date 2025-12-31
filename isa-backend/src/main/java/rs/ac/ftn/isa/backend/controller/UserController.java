// preuzeto iz vezbi 2

package rs.ac.ftn.isa.backend.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rs.ac.ftn.isa.backend.model.User;
import rs.ac.ftn.isa.backend.service.EmailService;

@RestController
@CrossOrigin(origins="*")
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup/async")
    public ResponseEntity<String> signUpAsync(@RequestBody User user){
        System.out.println("Thread id: " + Thread.currentThread().getId());
        try {
            emailService.sendNotificationAsync(user);
        }catch( Exception e ){
            logger.info("Greska prilikom slanja emaila: " + e.getMessage());
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @PostMapping("/signup/sync")
    public ResponseEntity<String> signUpSync(@RequestBody User user){
        System.out.println("Thread id: " + Thread.currentThread().getId());
        try {
            emailService.sendNotificationSync(user);
        }catch( Exception e ){
            logger.info("Greska prilikom slanja emaila: " + e.getMessage());
        }

        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
