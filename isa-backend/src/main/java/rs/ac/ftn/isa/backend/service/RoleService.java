package rs.ac.ftn.isa.backend.service;

import rs.ac.ftn.isa.backend.model.Role;

import java.util.List;



public interface RoleService {
    Role findById(Long id);
    List<Role> findByName(String name);
}