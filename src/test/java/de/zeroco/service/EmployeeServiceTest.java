package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee("John", "Doe", "john.doe@example.com", "12345", LocalDate.now(), "Developer", 70000.0);
        employee1.setId(1L);

        employee2 = new Employee("Jane", "Smith", "jane.smith@example.com", "67890", LocalDate.now(), "Manager", 90000.0);
        employee2.setId(2L);
    }

    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee1, employee2));
        List<Employee> employees = employeeService.getAllEmployees();
        assertEquals(2, employees.size());
        verify(employeeRepository).findAll();
    }

    @Test
    void getAllEmployees_shouldReturnEmptyList() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        List<Employee> employees = employeeService.getAllEmployees();
        assertTrue(employees.isEmpty());
        verify(employeeRepository).findAll();
    }

    @Test
    void getEmployeeById_shouldReturnEmployee_whenFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        Employee found = employeeService.getEmployeeById(1L);
        assertNotNull(found);
        assertEquals("john.doe@example.com", found.getEmail());
        verify(employeeRepository).findById(1L);
    }

    @Test
    void getEmployeeById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(3L));
        verify(employeeRepository).findById(3L);
    }

    @Test
    void createEmployee_shouldSaveAndReturnEmployee_whenEmailNotExists() {
        when(employeeRepository.findByEmail(employee1.getEmail())).thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee1);

        Employee created = employeeService.createEmployee(employee1);
        assertNotNull(created);
        assertEquals(employee1.getEmail(), created.getEmail());
        verify(employeeRepository).findByEmail(employee1.getEmail());
        verify(employeeRepository).save(employee1);
    }

    @Test
    void createEmployee_shouldThrowIllegalArgumentException_whenEmailExists() {
        Employee newEmployee = new Employee("Test", "User", "john.doe@example.com", "11111", LocalDate.now(), "Tester", 50000.0);
        when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(employee1));

        assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(newEmployee));
        verify(employeeRepository).findByEmail("john.doe@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldUpdateAndReturnEmployee_whenFoundAndEmailNotChangedOrAvailable() {
        Employee updatedDetails = new Employee("John", "DoeUpdated", "john.doe@example.com", "54321", LocalDate.now().minusDays(10), "Senior Developer", 75000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1)); // employee1 has original details
        when(employeeRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(employee1)); // Email not changed, belongs to current user
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee updated = employeeService.updateEmployee(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("John DoeUpdated", updated.getLastName());
        assertEquals(75000.0, updated.getSalary());
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any(Employee.class));
    }
    
    @Test
    void updateEmployee_shouldUpdateAndReturnEmployee_whenEmailChangedAndAvailable() {
        Employee updatedDetails = new Employee("John", "Doe", "john.new@example.com", "12345", LocalDate.now(), "Developer", 70000.0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findByEmail("john.new@example.com")).thenReturn(Optional.empty()); // New email is available
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Employee updated = employeeService.updateEmployee(1L, updatedDetails);

        assertEquals("john.new@example.com", updated.getEmail());
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).findByEmail("john.new@example.com");
        verify(employeeRepository).save(any(Employee.class));
    }


    @Test
    void updateEmployee_shouldThrowResourceNotFoundException_whenNotFound() {
        Employee updatedDetails = new Employee("Non", "Existent", "non.existent@example.com", "00000", LocalDate.now(), "Ghost", 0.0);
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.updateEmployee(3L, updatedDetails));
        verify(employeeRepository).findById(3L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void updateEmployee_shouldThrowIllegalArgumentException_whenEmailChangedAndTakenByAnother() {
        Employee updatedDetails = new Employee("John", "Doe", "jane.smith@example.com", "12345", LocalDate.now(), "Developer", 70000.0);
        // employee1 is being updated, trying to take employee2's email
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.of(employee2)); // employee2 already has this email

        assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1L, updatedDetails));
        verify(employeeRepository).findById(1L);
        verify(employeeRepository).findByEmail("jane.smith@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee_whenFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        doNothing().when(employeeRepository).delete(employee1);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).delete(employee1);
    }

    @Test
    void deleteEmployee_shouldThrowResourceNotFoundException_whenNotFound() {
        when(employeeRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> employeeService.deleteEmployee(3L));
        verify(employeeRepository).findById(3L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
