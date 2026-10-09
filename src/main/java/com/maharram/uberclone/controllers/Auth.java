package com.maharram.uberclone.controllers;

import com.maharram.uberclone.dtos.UserDTO;
import com.maharram.uberclone.services.JwtService;
import com.maharram.uberclone.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class Auth {

  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public Auth(
      UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/register")
  public ResponseEntity<Object> registerUser(
      @Valid @RequestBody UserDTO.CreateRequest createRequest) {
    UserDTO.CreateResponse response = userService.create(createRequest);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public String AuthenticateAndGetToken(@RequestBody UserDTO.AuthRequest authRequest) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                authRequest.username(), authRequest.password()));
    if (authentication.isAuthenticated()) {
      return jwtService.generateToken(authRequest.username());
    }
    throw new UsernameNotFoundException("UserEmail or Password not Found!");
  }
}
