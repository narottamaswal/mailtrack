package com.mailtrack.service;

import com.mailtrack.model.AppUser;
import com.mailtrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public AppUser upsert(String email, String name, String picture) {
        return userRepository.findByEmail(email).map(existing -> {
            existing.setName(name);
            existing.setPicture(picture);
            existing.setLastLoginAt(LocalDateTime.now());
            return userRepository.save(existing);
        }).orElseGet(() -> {
            AppUser user = new AppUser();
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
            return userRepository.save(user);
        });
    }

    public AppUser getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

}