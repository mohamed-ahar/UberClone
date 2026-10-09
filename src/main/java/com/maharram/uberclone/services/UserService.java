package com.maharram.uberclone.services;

import com.maharram.uberclone.dtos.UserDTO;
import com.maharram.uberclone.exceptions.ResourceNotFoundException;
import com.maharram.uberclone.models.User;
import com.maharram.uberclone.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // CREATE
  @Transactional
  public UserDTO.CreateResponse create(UserDTO.CreateRequest createRequest) {
    if (this.userRepository.existsByEmail(createRequest.email())) {
      throw new IllegalArgumentException("Email already exists");
    }
    String encodedPassword = this.passwordEncoder.encode(createRequest.password());
    User user =
        User.builder()
            .firstName(createRequest.firstname())
            .lastName(createRequest.lastname())
            .age(createRequest.age())
            .email(createRequest.email())
            .password(encodedPassword)
            .phoneNumber(createRequest.phoneNumber())
            .role(createRequest.role().toString())
            .build();
    this.userRepository.save(user);
    return new UserDTO.CreateResponse(
        user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
  }

  // READ by ID
  @Transactional(readOnly = true)
  public UserDTO.Response findById(UUID id) {
    User user = this.userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    return UserDTO.Response.fromEntity(user);
  }

  // READ by Email
  @Transactional(readOnly = true)
  public UserDTO.Response findByEmail(String email) {
    User user = this.userRepository.findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    return UserDTO.Response.fromEntity(user);
  }

  // READ ALL
  @Transactional(readOnly = true)
  public List<UserDTO.Response> findAll() {
    return this.userRepository.findAll().stream()
        .map(UserDTO.Response::fromEntity)
        .toList();
  }

  // UPDATE
  @Transactional
  public UserDTO.Response update(UUID id, UserDTO.UpdateRequest updateRequest) {
    User user = this.userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

    if (updateRequest.firstname() != null && !updateRequest.firstname().isBlank()) {
      user.setFirstName(updateRequest.firstname());
    }
    if (updateRequest.lastname() != null && !updateRequest.lastname().isBlank()) {
      user.setLastName(updateRequest.lastname());
    }
    if (updateRequest.age() != null) {
      user.setAge(updateRequest.age());
    }
    if (updateRequest.phoneNumber() != null) {
      user.setPhoneNumber(updateRequest.phoneNumber());
    }
    if (updateRequest.role() != null) {
      user.setRole(updateRequest.role().toString());
    }

    User updatedUser = this.userRepository.save(user);
    return UserDTO.Response.fromEntity(updatedUser);
  }

  // DELETE
  @Transactional
  public void delete(UUID id) {
    if (!this.userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found with id: " + id);
    }
    this.userRepository.deleteById(id);
  }
}
