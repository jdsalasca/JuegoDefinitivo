package com.juegodefinitivo.autobook.security;

public enum ApiRole {
    STUDENT,
    TEACHER,
    ADMIN;

    public boolean canAccess(ApiRole requiredRole) {
        if (this == ADMIN) {
            return true;
        }
        if (this == TEACHER) {
            return requiredRole == TEACHER || requiredRole == STUDENT;
        }
        return requiredRole == STUDENT;
    }
}
