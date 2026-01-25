package rs.ac.ftn.isa.backend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.service.UserService;
import rs.ac.ftn.isa.backend.dto.PublicUserDto;
import rs.ac.ftn.isa.backend.service.VideoPostService;
import rs.ac.ftn.isa.backend.domain.model.VideoPost;


// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserController {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoPostService videoPostService;

    @GetMapping("/whoami")
    public User user(Principal user) {
        return this.userService.findByEmail(user.getName());
    }

    @GetMapping("/foo")
    public Map<String, String> getFoo() {
        Map<String, String> fooObj = new HashMap<>();
        fooObj.put("foo", "bar");
        return fooObj;
    }

    @GetMapping("/users/{userId}/public")
    public PublicUserDto publicProfile(@PathVariable Long userId) {
        User u = this.userService.findById(userId);
        return new PublicUserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail());
    }

    @GetMapping("/users/{userId}/videos")
    public List<VideoPost> getUserVideos(@PathVariable Long userId) {
        return videoPostService.findByOwnerId(userId);
    }

}