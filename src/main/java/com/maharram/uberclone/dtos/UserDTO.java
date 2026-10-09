package com.maharram.uberclone.dtos;

import com.maharram.uberclone.models.User;
import com.maharram.uberclone.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;
import java.util.UUID;

public class UserDTO {
  public enum UserRole {
    USER_ROLE,
    ADMIN_ROLE,
    VIEWER_ROLE,
    DRIVER_ROLE
  }

  public record CreateRequest(
      @NotBlank(message = "FirstName is Empty") String firstname,
      @NotBlank(message = "LastName is Empty") String lastname,
      @NotNull(message = "Age not specified") @Min(value = 18, message = "Age must be at least 18") Integer age,
      @Email(message = "Invalid email format") String email,
      @ValidPassword String password,
      String phoneNumber,
      UserRole role) {
      public CreateRequest {
        if (role == null) {
          role = UserRole.USER_ROLE;
        }
      }
  }

  public record UpdateRequest(
      String firstname,
      String lastname,
      Integer age,
      String phoneNumber,
      UserRole role
  ) {}

  public record CreateResponse(UUID id, String firstname, String lastname, String email) {}

  public record Response(
      UUID id,
      String firstname,
      String lastname,
      Integer age,
      String email,
      String phoneNumber,
      String role,
      Date joinDate
  ) {
    public static Response fromEntity(User user) {
      if (user == null) return null;
      return new Response(
          user.getId(),
          user.getFirstName(),
          user.getLastName(),
          user.getAge(),
          user.getEmail(),
          user.getPhoneNumber(),
          user.getRole(),
          user.getJoinDate()
      );
    }
  }

  public record AuthRequest(@Email String username, @ValidPassword String password) {}
}
