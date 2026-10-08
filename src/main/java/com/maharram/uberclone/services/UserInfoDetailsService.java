package com.maharram.uberclone.services;

import com.maharram.uberclone.models.User;
import com.maharram.uberclone.models.UserInfoDetails;
import com.maharram.uberclone.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserInfoDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  public UserInfoDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

    return new UserInfoDetails(user);
  }
}
