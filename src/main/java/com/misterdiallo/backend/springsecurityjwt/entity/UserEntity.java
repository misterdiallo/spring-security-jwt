package com.misterdiallo.backend.springsecurityjwt.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserEntity {
    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

}
