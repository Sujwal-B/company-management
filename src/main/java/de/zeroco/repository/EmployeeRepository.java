package de.zeroco.repository;

import de.zeroco.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Example of a custom query method (optional for now, but good to have as an example)
    Optional<Employee> findByEmail(String email);
}
