package rs.ac.ftn.isa.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.domain.model.TranscodingJob;

import java.util.Optional;

public interface TranscodingJobRepository extends JpaRepository<TranscodingJob, Long> {
    Optional<TranscodingJob> findByJobId(String jobId);
}