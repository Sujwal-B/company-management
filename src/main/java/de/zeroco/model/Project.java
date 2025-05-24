package de.zeroco.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Represents a project in the company")
@Entity
@Table(name = "projects")
public class Project {

    @Schema(description = "Unique identifier of the project", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Name of the project", example = "New Website Launch", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Schema(description = "Detailed description of the project", example = "Launch of the new corporate website with enhanced features.")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Schema(description = "Start date of the project", example = "2023-01-10")
    @Column(name = "start_date")
    private LocalDate startDate;

    @Schema(description = "End date of the project", example = "2023-06-30")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Schema(description = "Set of employees assigned to this project. Managed via specific assignment endpoints.", accessMode = Schema.AccessMode.READ_ONLY)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_employee",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    // Constructors
    public Project() {
    }

    public Project(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }

    // Helper methods for managing the bidirectional relationship
    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.getProjectsInternal().add(this); // Use internal getter if Employee manages its side
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.getProjectsInternal().remove(this); // Use internal getter
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id) &&
               Objects.equals(name, project.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Project{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               // Avoid printing employees to prevent recursion and large logs
               ", employee_count=" + (employees != null ? employees.size() : 0) +
               '}';
    }
}
