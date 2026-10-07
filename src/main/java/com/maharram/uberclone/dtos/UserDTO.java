package com.maharram.uberclone.dtos;

import com.maharram.uberclone.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class UserDTO {

    public record CreateRequest(
            @NotBlank(message = "FirstName is Empty") String firstname,
            @NotBlank(message = "LastName is Empty") String lastname,
            @Email(message = "") String email,
            @ValidPassword String password){
    };
    public record CreateResponse(
            UUID id,
            String firstname,
            String lastname,
            String email
    ){};
}
