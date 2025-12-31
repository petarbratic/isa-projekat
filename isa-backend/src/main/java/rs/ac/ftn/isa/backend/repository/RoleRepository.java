package rs.ac.ftn.isa.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.model.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findByName(String name);
}