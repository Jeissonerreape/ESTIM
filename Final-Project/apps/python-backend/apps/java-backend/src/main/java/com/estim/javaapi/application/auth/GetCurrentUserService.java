package com.estim.javaapi.application.auth;

import com.estim.javaapi.domain.user.User;
import com.estim.javaapi.domain.user.UserId;
import com.estim.javaapi.domain.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Simple application service to fetch the current user's information.
 * The controller will supply the UserId extracted from the security context.
 */
@Service
public class GetCurrentUserService {

    private final UserRepository userRepository;

    public GetCurrentUserService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    public User getCurrentUser(UserId userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
