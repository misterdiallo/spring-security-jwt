package com.misterdiallo.backend.springsecurityjwt.service;


import com.misterdiallo.backend.springsecurityjwt.config.JwtService;
import com.misterdiallo.backend.springsecurityjwt.entity.CountryEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.RoleEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.UserEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.CountryRepository;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.UserRepository;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationRequest;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationResponse;
import com.misterdiallo.backend.springsecurityjwt.model.RegisterUserRequest;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final AuthenticationManager authenticationManager;

    // Register User
    public AuthenticationResponse register(RegisterUserRequest request) {
        Optional<CountryEntity> country = countryRepository.findByCode(request.getCountry());
        if(country.isPresent()) {

            var user = UserEntity.builder()
                    .firstname(request.getFirstname())
                    .lastname(request.getLastname())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .country(country.get())
                    .role(RoleEntity.USER)
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();
            userRepository.save(user);

            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }else {
            return null;
        }
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmailOrUsernameOrPhone(request.getUsername(), request.getUsername(), request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
