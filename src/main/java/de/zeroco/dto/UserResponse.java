package de.zeroco.dto;

import java.util.Objects;

public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, String firstName, String lastName, String roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserResponse that = (UserResponse) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(username, that.username) &&
               Objects.equals(email, that.email) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(roles, that.roles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, firstName, lastName, roles);
    }

    @Override
    public String toString() {
        return "UserResponse{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", roles='" + roles + '\'' +
               '}';
    }
}
