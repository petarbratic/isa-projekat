package rs.ac.ftn.isa.backend.repository.projection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.ftn.isa.backend.domain.model.VideoView;
import rs.ac.ftn.isa.backend.repository.VideoViewRepository;
import rs.ac.ftn.isa.backend.service.PopularVideosEtlService;

@Service
public class PopularVideosEtlServiceImpl implements PopularVideosEtlService {

    @Autowired
    private VideoViewRepository videoViewRepository;

    @Autowired
    public PopularVideosEtlServiceImpl(VideoViewRepository videoViewRepository){
        this.videoViewRepository = videoViewRepository;
    }

    @Override
    public void runDailyPipeline(){
        // Extract
        // Transform
        // Load
    }
}

