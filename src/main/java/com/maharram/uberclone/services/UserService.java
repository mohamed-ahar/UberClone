package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.UserDTO;
import com.maharram.uberclone.models.User;
import com.maharram.uberclone.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserDTO.CreateResponse create(UserDTO.CreateRequest createRequest) {
        if (this.userRepository.existsByEmail(createRequest.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        String encodedPassword = this.passwordEncoder.encode(createRequest.password());
        User user = User.builder().firstName(createRequest.firstname()).lastName(createRequest.lastname()).email(createRequest.email()).password(encodedPassword).build();
        this.userRepository.save(user);
        return new UserDTO.CreateResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }
}
