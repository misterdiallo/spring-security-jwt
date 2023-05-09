package com.misterdiallo.backend.springsecurityjwt.entity.repository;

import com.misterdiallo.backend.springsecurityjwt.entity.CountryEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, Integer> {
    Optional<CountryEntity> findByCode(String code);
}
