package com.misterdiallo.backend.springsecurityjwt;

import com.misterdiallo.backend.springsecurityjwt.entity.CountryEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.RoleEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.repository.CountryRepository;
import com.misterdiallo.backend.springsecurityjwt.model.RegisterUserRequest;
import com.misterdiallo.backend.springsecurityjwt.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import static com.misterdiallo.backend.springsecurityjwt.entity.RoleEntity.*;

@SpringBootApplication
public class SpringSecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityJwtApplication.class, args);
    }


    @Bean
    public CommandLineRunner commandLineRunner(
            AuthenticationService authenticationService,
            CountryRepository countryRepository
    ) {
        return args -> {
            // New Country
            var country = CountryEntity
                    .builder()
                    .code("86")
                    .name("china")
                    .build();
            // Print new saved Country
            System.out.println("New Country created: " + countryRepository.save(country).getName());




            // New ADMIN
            var admin = RegisterUserRequest
                    .builder()
                    .firstname("Admin")
                    .lastname("Admin")
                    .email("admin@mail.com")
                    .country("86")
                    .phone("18354919286")
                    .username("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            // Print the new saved ADMIN's Access Token
            System.out.println("Admin Access Token: " + authenticationService.register(admin).getAccessToken());



            // New MANAGER
            var manager = RegisterUserRequest
                    .builder()
                    .firstname("Manager")
                    .lastname("Manager")
                    .email("manager@mail.com")
                    .country("86")
                    .phone("18354919285")
                    .username("manager@mail.com")
                    .password("password")
                    .role(MANAGER)
                    .build();
            // Print the new saved MANAGER's Access Token
            System.out.println("Manager Access Token: " + authenticationService.register(manager).getAccessToken());


            // New USER
            var user = RegisterUserRequest
                    .builder()
                    .firstname("User")
                    .lastname("User")
                    .email("user@mail.com")
                    .country("86")
                    .phone("18354919284")
                    .username("user@mail.com")
                    .password("password")
                    .role(USER)
                    .build();
            // Print the new saved USER's Access Token
            System.out.println("user Access Token: " + authenticationService.register(user).getAccessToken());
        };
    }
}
