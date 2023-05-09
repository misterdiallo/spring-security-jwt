package com.misterdiallo.backend.springsecurityjwt.controller;


import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationRequest;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationResponse;
import com.misterdiallo.backend.springsecurityjwt.model.RegisterUserRequest;
import com.misterdiallo.backend.springsecurityjwt.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterUserRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
