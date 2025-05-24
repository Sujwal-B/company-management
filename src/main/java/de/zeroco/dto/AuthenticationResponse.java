package de.zeroco.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing the JWT authentication token")
public class AuthenticationResponse {

    @Schema(description = "JSON Web Token (JWT) for the authenticated user", 
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VybmFtZSIsImV4cCI6MTYxNjQwNjQwMCwiaWF0IjoxNjE2NDA2MDQwfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    private final String jwt;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
