package rs.ac.ftn.isa.backend.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.dto.UserRequest;
import rs.ac.ftn.isa.backend.model.Role;
import rs.ac.ftn.isa.backend.model.User;
import rs.ac.ftn.isa.backend.repository.UserRepository;
import rs.ac.ftn.isa.backend.service.RoleService;
import rs.ac.ftn.isa.backend.service.UserService;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public User findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }

    @Override
    public User save(UserRequest userRequest) {
        User u = new User();
        u.setUsername(userRequest.getUsername());

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setFirstName(userRequest.getFirstname());
        u.setLastName(userRequest.getLastname());
        u.setEnabled(false);
        u.setEmail(userRequest.getEmail());
        u.setAddress(userRequest.getAddress());
        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER
        List<Role> roles = roleService.findByName("ROLE_USER");
        u.setRoles(roles);
        u.setActivationToken(java.util.UUID.randomUUID().toString());

        return this.userRepository.save(u);
    }

    @Override
    public User findByActivationToken(String token) throws UsernameNotFoundException {
        return userRepository.findByActivationToken(token);
    }


    @Override
    public User activateAccount(String token) {
        User u = userRepository.findByActivationToken(token);
        if (u == null) return null;

        u.setEnabled(true);
        u.setActivationToken(null);

        return userRepository.save(u);
    }

}