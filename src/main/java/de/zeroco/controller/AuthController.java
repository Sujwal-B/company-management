package de.zeroco.controller;

import de.zeroco.dto.AuthenticationRequest;
import de.zeroco.dto.AuthenticationResponse;
import de.zeroco.security.JwtUtil;
import de.zeroco.security.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import de.zeroco.dto.RegistrationRequest;
import de.zeroco.dto.UserResponse;
import de.zeroco.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "API for user authentication and token generation")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtil jwtUtil,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Operation(summary = "Authenticate user and generate JWT token",
               description = "Takes username and password, and returns a JWT token if authentication is successful.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, JWT token returned",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials",
                         content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                                            schema = @Schema(example = "Incorrect username or password")))
    })
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User credentials for authentication.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AuthenticationRequest.class))
            )
            @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Incorrect username or password");
        }

        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @Operation(summary = "Register a new user",
               description = "Creates a new user account with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., validation errors)",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Object.class))), // Or a more specific error DTO
            @ApiResponse(responseCode = "409", description = "User or email already exists",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Object.class))) // Or a more specific error DTO
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.registerUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
