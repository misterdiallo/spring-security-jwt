package com.misterdiallo.backend.springsecurityjwt.entity.repository;

import com.misterdiallo.backend.springsecurityjwt.entity.CountryEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.TokenEntity;
import com.misterdiallo.backend.springsecurityjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Integer> {

    @Query("""
    select t from TokenEntity t inner join UserEntity  u on t.user.id = u.id
    where u.id = :userId and (t.expired = false or t.revoked = false)
    """)
    List<TokenEntity> findAllValidTokensByUser(Integer userId);


    Optional<TokenEntity> findByToken(String token);

}
