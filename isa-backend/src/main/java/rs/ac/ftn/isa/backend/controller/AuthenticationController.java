package rs.ac.ftn.isa.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import rs.ac.ftn.isa.backend.dto.JwtAuthenticationRequest;
import rs.ac.ftn.isa.backend.dto.UserRequest;
import rs.ac.ftn.isa.backend.dto.UserTokenState;
import rs.ac.ftn.isa.backend.exception.ResourceConflictException;
import rs.ac.ftn.isa.backend.model.User;
import rs.ac.ftn.isa.backend.service.UserService;
import rs.ac.ftn.isa.backend.util.TokenUtils;




//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    // promenio sam u email i  lozinku
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getEmail(),
                            authenticationRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String jwt = tokenUtils.generateToken(user.getEmail());
            int expiresIn = tokenUtils.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (DisabledException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Nalog nije aktiviran. Proverite email za aktivaciju.");

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Pogrešan email ili lozinka.");
        }
    }

    // Endpoint za registraciju novog korisnika
    @PostMapping("/signup")
    public ResponseEntity<User> addUser(@RequestBody UserRequest userRequest, UriComponentsBuilder ucBuilder) {
        User existUser = this.userService.findByEmail(userRequest.getEmail());

        if (existUser != null) {
            throw new ResourceConflictException(userRequest.getId(), "Email already exists");
        }

        User user = this.userService.save(userRequest);

        System.out.println(
                "ACTIVATION LINK: http://localhost:4200/activate?token=" + user.getActivationToken()
        );

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String token) {
        User activated = userService.activateAccount(token);

        if (activated == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid activation token.");
        }

        return ResponseEntity.ok("Account activated.");
    }
}
