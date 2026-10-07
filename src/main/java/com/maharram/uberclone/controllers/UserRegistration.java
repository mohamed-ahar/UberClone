package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.UserDTO;
import com.maharram.uberclone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/register")
public class UserRegistration {
    private final UserService userService;
    public UserRegistration(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/")
    public ResponseEntity<Object> registerUser(@Valid @RequestBody UserDTO.CreateRequest createRequest) {
        UserDTO.CreateResponse response = userService.create(createRequest);
        return ResponseEntity.ok(response);
    }
}
