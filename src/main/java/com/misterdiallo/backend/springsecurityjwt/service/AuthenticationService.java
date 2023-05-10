package com.misterdiallo.backend.springsecurityjwt.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.misterdiallo.backend.springsecurityjwt.config.JwtService;
import com.misterdiallo.backend.springsecurityjwt.entity.*;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.CountryRepository;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.TokenRepository;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.UserRepository;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationRequest;
import com.misterdiallo.backend.springsecurityjwt.model.AuthenticationResponse;
import com.misterdiallo.backend.springsecurityjwt.model.RegisterUserRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;
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

    private final Environment env;

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

            var jwtToken = jwtService.generateToken(newUser);
            var refreshToken = jwtService.generateRefreshToken(newUser);

            saveUserToken(newUser, jwtToken, refreshToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
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
        //Generate the refresh token
        var refreshToken = jwtService.generateRefreshToken(user);
        // Revoke all existing tokens for the user.
        revokeAllUserTokens(user);
        // Save the token for the user
        saveUserToken(user, jwtToken, refreshToken);
        // Return the response with the token
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    // For saving new token for a specific user in the database
    private void saveUserToken(UserEntity user, String jwtToken, String refreshToken) {
        // Creating the token
        var token = TokenEntity
                .builder()
                .user(user)
                .token(jwtToken)
                .refresh(refreshToken)
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

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if(authenticationHeader == null || !authenticationHeader.startsWith(Objects.requireNonNull(env.getProperty("my-config.request.header.should_start_with")))) {
            return;
        }
        refreshToken  = authenticationHeader.substring(Objects.requireNonNull(env.getProperty("my-config.request.header.should_start_with")).length() + 1);
        username = jwtService.extractUsername(refreshToken);
        if(username != null ) {
            var userDetails = this.userRepository.findByEmailOrUsernameOrPhone(username,username,username)
                    .orElseThrow();
            var isRefreshTokenValidInDB = tokenRepository.findByRefresh(refreshToken)
                    .map(t -> !t.isExpired() && !t.isRevoked())
                    .orElse(false);
            if(
                    jwtService.isTokenValid(refreshToken, userDetails)
                            && isRefreshTokenValidInDB
            ) {
                var accessToken = jwtService.generateToken(userDetails);
                var newRefreshToken = jwtService.generateRefreshToken(userDetails);
                // Revoke all existing tokens for the user.
                revokeAllUserTokens(userDetails);
                // Save the token for the user
                saveUserToken(userDetails, accessToken, newRefreshToken);
                var authResponse = AuthenticationResponse
                        .builder()
                        .accessToken(accessToken)
                        .refreshToken(newRefreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
