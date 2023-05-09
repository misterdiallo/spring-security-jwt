package com.misterdiallo.backend.springsecurityjwt.entity.repository;

import com.misterdiallo.backend.springsecurityjwt.entity.CountryEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.UserEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByCountryAndPhone(CountryEntity country, String phone);

    Optional<UserEntity> findByEmailOrUsernameOrPhone(String email, String username, String phone);
}
