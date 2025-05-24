package de.zeroco.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Schema(description = "Represents an employee in the company")
@Entity
@Table(name = "employees")
// Using JsonIgnoreProperties for simplicity to handle potential Jackson recursion with bidirectional relationships.
// "projects" refers to the field name in this Employee class.
@JsonIgnoreProperties(value = {"projects"}, allowSetters = true)
public class Employee {

    @Schema(description = "Unique identifier of the employee", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "First name of the employee", example = "Jane", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Schema(description = "Last name of the employee", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Schema(description = "Email address of the employee", example = "jane.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Schema(description = "Phone number of the employee", example = "555-1234")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Schema(description = "Date when the employee was hired", example = "2022-08-15")
    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Schema(description = "Job title of the employee", example = "Software Engineer")
    @Column(name = "job_title", length = 100)
    private String jobTitle;

    @Schema(description = "Salary of the employee", example = "75000.00")
    @Column(name = "salary")
    private Double salary;

    @Schema(description = "Set of projects the employee is assigned to. Managed via specific project assignment endpoints.", accessMode = Schema.AccessMode.READ_ONLY)
    @ManyToMany(mappedBy = "employees", fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    // JPA requires a no-arg constructor
    public Employee() {
    }

    // Constructor with all fields (except id as it's auto-generated)
    public Employee(String firstName, String lastName, String email, String phoneNumber,
                    LocalDate hireDate, String jobTitle, Double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hireDate = hireDate;
        this.jobTitle = jobTitle;
        this.salary = salary;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    // Internal getter for Project entity to maintain bidirectional consistency
    // Not strictly public API, used by Project's addEmployee/removeEmployee
    Set<Project> getProjectsInternal() {
        if (this.projects == null) { // Should be initialized, but defensive
            this.projects = new HashSet<>();
        }
        return this.projects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
               Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Employee{" +
               "id=" + id +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", hireDate=" + hireDate +
               ", jobTitle='" + jobTitle + '\'' +
               ", salary=" + salary +
               ", project_count=" + (projects != null ? projects.size() : 0) + // Avoid direct serialization of projects
               '}';
    }
}
