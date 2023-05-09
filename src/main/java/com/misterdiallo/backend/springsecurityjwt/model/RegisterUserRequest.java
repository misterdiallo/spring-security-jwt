package com.misterdiallo.backend.springsecurityjwt.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String country;
    private String phone;
    private String username;
    private String password;

}
