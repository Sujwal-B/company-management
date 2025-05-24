package de.zeroco.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.Objects;

@Schema(description = "Represents a department within the company")
@Entity
@Table(name = "departments")
public class Department {

    @Schema(description = "Unique identifier of the department", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the department", example = "Human Resources", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Location of the department", example = "Building A, Floor 2")
    @Column(name = "location", length = 100)
    private String location;

    // JPA requires a no-arg constructor
    public Department() {
    }

    public Department(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Department that = (Department) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Department{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", location='" + location + '\'' +
               '}';
    }
}
