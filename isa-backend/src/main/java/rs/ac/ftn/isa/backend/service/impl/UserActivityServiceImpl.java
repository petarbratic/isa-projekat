package rs.ac.ftn.isa.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.ac.ftn.isa.backend.domain.model.User;
import rs.ac.ftn.isa.backend.domain.model.UserActivity;
import rs.ac.ftn.isa.backend.repository.UserActivityRepository;
import rs.ac.ftn.isa.backend.repository.UserRepository;
import rs.ac.ftn.isa.backend.service.UserActivityService;

import java.time.LocalDateTime;

@Service
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final UserRepository userRepository;

    public UserActivityServiceImpl(UserActivityRepository userActivityRepository,
                                   UserRepository userRepository) {
        this.userActivityRepository = userActivityRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void touch(Long userId) {
        // Ako user ne postoji, samo izadji (ne rusimo request)
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        LocalDateTime now = LocalDateTime.now();

        UserActivity ua = userActivityRepository.findByUserId(userId)
                .orElseGet(() -> new UserActivity(user, now));

        ua.setLastActivity(now);
        // ua vec ima user setovan u konstruktoru; ali za slucaj da je ucitan iz baze, ostaje
        if (ua.getUser() == null) {
            ua.setUser(user);
        }

        userActivityRepository.save(ua);
    }
}