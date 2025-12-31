package rs.ac.ftn.isa.backend.service;

import rs.ac.ftn.isa.backend.dto.UserRequest;
import rs.ac.ftn.isa.backend.model.User;

import java.util.List;



public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll ();
    User save(UserRequest userRequest);
}