package de.zeroco.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request object for user authentication")
public class AuthenticationRequest {

    @Schema(description = "Username of the user", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "Password of the user", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    // No-arg constructor for JSON deserialization
    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
