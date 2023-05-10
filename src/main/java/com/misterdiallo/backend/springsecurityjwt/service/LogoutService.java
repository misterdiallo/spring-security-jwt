package com.misterdiallo.backend.springsecurityjwt.service;

import com.misterdiallo.backend.springsecurityjwt.entity.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LogoutService  implements LogoutHandler {

    private final Environment env;
    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authenticationHeader = request.getHeader(env.getProperty("my-config.request.header.authorization_name"));
        final String jwt;
        if(authenticationHeader == null || !authenticationHeader.startsWith(Objects.requireNonNull(env.getProperty("my-config.request.header.should_start_with")))) {
            return;
        }
        jwt  = authenticationHeader.substring(Objects.requireNonNull(env.getProperty("my-config.request.header.should_start_with")).length() + 1);
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null);
        if(storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
