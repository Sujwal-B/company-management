package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        // Check if email already exists
        Optional<Employee> existingEmployeeByEmail = employeeRepository.findByEmail(employee.getEmail());
        if (existingEmployeeByEmail.isPresent()) {
            throw new IllegalArgumentException("Employee with email " + employee.getEmail() + " already exists.");
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee existingEmployee = getEmployeeById(id); // This will throw ResourceNotFoundException if not found

        // Update fields
        existingEmployee.setFirstName(employeeDetails.getFirstName());
        existingEmployee.setLastName(employeeDetails.getLastName());
        
        // Check if the new email is different and if it already exists for another employee
        if (!existingEmployee.getEmail().equals(employeeDetails.getEmail())) {
            Optional<Employee> employeeByNewEmail = employeeRepository.findByEmail(employeeDetails.getEmail());
            if (employeeByNewEmail.isPresent() && !employeeByNewEmail.get().getId().equals(existingEmployee.getId())) {
                throw new IllegalArgumentException("Email " + employeeDetails.getEmail() + " is already in use by another employee.");
            }
            existingEmployee.setEmail(employeeDetails.getEmail());
        }
        
        existingEmployee.setPhoneNumber(employeeDetails.getPhoneNumber());
        existingEmployee.setHireDate(employeeDetails.getHireDate());
        existingEmployee.setJobTitle(employeeDetails.getJobTitle());
        existingEmployee.setSalary(employeeDetails.getSalary());

        return employeeRepository.save(existingEmployee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee existingEmployee = getEmployeeById(id); // This will throw ResourceNotFoundException if not found
        employeeRepository.delete(existingEmployee);
    }
}
