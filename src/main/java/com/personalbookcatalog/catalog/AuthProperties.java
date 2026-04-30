package com.personalbookcatalog.catalog;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Auth settings for the single admin account.
 */
@Validated
@ConfigurationProperties(prefix = "app.auth.admin")
public class AuthProperties {

    @NotBlank(message = "{validation.admin.username.required}")
    private String username;

    @NotBlank(message = "{validation.admin.password.required}")
    private String passwordHash;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
