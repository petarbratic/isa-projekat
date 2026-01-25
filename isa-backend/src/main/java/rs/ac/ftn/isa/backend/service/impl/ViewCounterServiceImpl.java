package rs.ac.ftn.isa.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.domain.model.ViewCounter;
import rs.ac.ftn.isa.backend.repository.ViewCounterRepository;
import rs.ac.ftn.isa.backend.service.ViewCounterService;

@Service
public class ViewCounterServiceImpl implements ViewCounterService {

    private final ViewCounterRepository repo;

    public ViewCounterServiceImpl(ViewCounterRepository repo) {
        this.repo = repo;
    }

    private ViewCounter load() {
        return repo.findById(1L).orElseGet(() -> repo.save(new ViewCounter()));
    }

    @Override
    @Transactional
    public long incrementLocal() {
        ViewCounter c = load();
        c.setValue(c.getValue() + 1);
        return c.getValue();
    }

    @Override
    public long getLocal() {
        return load().getValue();
    }
}
