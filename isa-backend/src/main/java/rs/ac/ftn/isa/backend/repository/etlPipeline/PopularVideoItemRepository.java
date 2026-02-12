package rs.ac.ftn.isa.backend.repository.etlPipeline;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.ftn.isa.backend.domain.model.etlPipeline.PopularVideoItem;

public interface PopularVideoItemRepository extends JpaRepository<PopularVideoItem, Long> {}