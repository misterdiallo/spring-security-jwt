package com.misterdiallo.backend.springsecurityjwt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.misterdiallo.backend.springsecurityjwt.entity.PermissionEntity.*;

@RequiredArgsConstructor
public enum RoleEntity {
    USER(Collections.emptySet()),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_CREATE,
                    MANAGER_UPDATE,
                    MANAGER_DELETE
            )
    ),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    MANAGER_READ,
                    MANAGER_CREATE,
                    MANAGER_UPDATE,
                    MANAGER_DELETE
            )
    ),
    SUPER_ADMIN(Collections.emptySet()),

    ;

    @Getter
    private final Set<PermissionEntity> permissionEntities;

    public List<SimpleGrantedAuthority>  getAuthorities() {
        var authorities = getPermissionEntities()
                .stream()
                .map(permissionEntities -> new SimpleGrantedAuthority((permissionEntities.getPermission())))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }


}
