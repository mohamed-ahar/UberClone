package com.maharram.uberclone.models;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserInfoDetails implements UserDetails {
  private final String username;
  private final String password;
  private final List<GrantedAuthority> authorities;

  public UserInfoDetails(User user) {
    this.username = user.getEmail();
    this.password = user.getPassword();
    this.authorities =
        user.getRole() != null
            ? Stream.of(user.getRole())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList())
            : List.of();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }
}
