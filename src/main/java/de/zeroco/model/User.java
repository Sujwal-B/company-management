package de.zeroco.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.Objects;

@Schema(description = "Represents a user in the system")
@Entity
@Table(name = "users")
public class User {

    @Schema(description = "Unique identifier of the user", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Username of the user", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Schema(description = "Password of the user (hashed). Not typically exposed in responses.", hidden = true)
    @Column(nullable = false, length = 100) // Length might need adjustment based on password encoding
    private String password;

    @Schema(description = "Email address of the user", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Schema(description = "First name of the user", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false, length = 50)
    private String firstName;

    @Schema(description = "Last name of the user", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(nullable = false, length = 50)
    private String lastName;

    @Schema(description = "Comma-separated list of roles assigned to the user", example = "ROLE_USER,ROLE_ADMIN")
    @Column(length = 200) // To store comma-separated roles
    private String roles;

    // JPA requires a no-arg constructor
    public User() {
    }

    public User(String username, String password, String email, String firstName, String lastName, String roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    public User(Long id, String username, String password, String email, String firstName, String lastName, String roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
               Objects.equals(username, user.username) &&
               Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               // Do not include password in toString for security reasons
               ", roles='" + roles + '\'' +
               '}';
    }
}
