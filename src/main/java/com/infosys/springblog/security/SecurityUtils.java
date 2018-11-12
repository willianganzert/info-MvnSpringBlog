package com.infosys.springblog.security;

import java.util.Optional;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static Optional<String> getCurrentUserLogin() {
        return Optional.of("user");
    }

    public static Optional<String> getCurrentUserJWT() {
        return Optional.of("HUAHUAHUAH");
    }

    public static boolean isAuthenticated() {
        return true;
    }

    public static boolean isCurrentUserInRole(String authority) {
        return true;
    }
}
