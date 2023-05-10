package com.misterdiallo.backend.springsecurityjwt.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PermissionEntity {
    //  ADMIN PERMISSIONS
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete"),

    //  MANAGER PERMISSIONS
    MANAGER_READ("manager:read"),
    MANAGER_CREATE("manager:create"),
    MANAGER_UPDATE("manager:update"),
    MANAGER_DELETE("manager:delete"),

    //  USER PERMISSIONS
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    ;

    @Getter
    private final String permission;
}
