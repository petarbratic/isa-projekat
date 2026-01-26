package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.domain.model.ViewCounter;

import java.util.List;

public interface ViewCounterRepository extends JpaRepository<ViewCounter, Long> {
    List<ViewCounter> findByDirtyTrue();
}