package de.zeroco.repository;

import de.zeroco.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByName(String name);

    // Consider adding methods for querying projects by employee if needed in the future, e.g.:
    // List<Project> findByEmployees_Id(Long employeeId);
    // List<Project> findByEmployees_Email(String employeeEmail);
}
