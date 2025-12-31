package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}