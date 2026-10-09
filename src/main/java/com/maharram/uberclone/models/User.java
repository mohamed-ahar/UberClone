package com.maharram.uberclone.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.sql.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String firstName;
  private String lastName;
  private Integer age;

  @Column(unique = true)
  private String email;

  private String password;

  private String phoneNumber;

  private String role;

  @CreatedDate
  private Date joinDate;

  @PrePersist
  protected void onCreate() {
    if (this.joinDate == null) {
      this.joinDate = new Date(System.currentTimeMillis());
    }
  }
}
