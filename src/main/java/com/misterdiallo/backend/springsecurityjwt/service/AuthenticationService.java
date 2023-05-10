package com.misterdiallo.backend.springsecurityjwt.service;


import com.misterdiallo.backend.springsecurityjwt.config.JwtService;
import com.misterdiallo.backend.springsecurityjwt.entity.*;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.CountryRepository;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.TokenRepository;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.UserRepository;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationRequest;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationResponse;
import com.misterdiallo.backend.springsecurityjwt.model.RegisterUserRequest;
import lombok.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
            var newUser = userRepository.save(user);

            var jwtToken = jwtService.generateToken(user);

            saveUserToken(newUser, jwtToken);

            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        }else {
            return null;
        }
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Authenticate the user in the system with spring
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        // find the user or throw error
        var user = userRepository.findByEmailOrUsernameOrPhone(request.getUsername(), request.getUsername(), request.getUsername())
                .orElseThrow();
        // Generate the token
        var jwtToken = jwtService.generateToken(user);
        // Revoke all existing tokens for the user.
        revokeAllUserTokens(user);
        // Save the token for the user
        saveUserToken(user, jwtToken);
        // Return the response with the token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // For saving new token for a specific user in the database
    private void saveUserToken(UserEntity user, String jwtToken) {
        // Creating the token
        var token = TokenEntity
                .builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.MISTERDIALLO)
                .expired(false)
                .revoked(false)
                .build();
        // Saving the token in database
        tokenRepository.save(token);
    }


    // For removing all existing tokens before adding new token in the database
    private void revokeAllUserTokens(UserEntity user) {
        // Get all the valid tokens in the database for the specific user
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if( validUserTokens.isEmpty())

            return;  // If the list is empty, then leave.
        // Else update all the existing tokens to expired and revoked
        validUserTokens.forEach(t -> {
                t.setExpired(true);
                t.setRevoked(true);
        });
        // Then save the update list of token in the database.
        tokenRepository.saveAll(validUserTokens);
    }

}
