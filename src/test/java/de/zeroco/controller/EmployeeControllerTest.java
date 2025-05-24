package de.zeroco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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

    // --- Unauthenticated Access Tests ---
    @Test
    void getAllEmployees_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createEmployee_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isUnauthorized());
    }

    // --- Authenticated Access Tests (ROLE_USER where applicable) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllEmployees_authenticated_shouldReturnListOfEmployees() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is(employee1.getEmail())));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllEmployees_authenticated_shouldReturnEmptyList() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getEmployeeById_authenticated_shouldReturnEmployee_whenFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(employee1);
        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getEmployeeById_authenticated_shouldReturnNotFound_whenNotFound() throws Exception {
        when(employeeService.getEmployeeById(3L)).thenThrow(new ResourceNotFoundException("Employee not found with id: 3"));
        mockMvc.perform(get("/api/employees/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found with id: 3")));
    }

    // --- Unauthorized Access Tests (ROLE_USER for ADMIN operations) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createEmployee_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void updateEmployee_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteEmployee_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());
    }

    // --- Admin Access Tests (ROLE_ADMIN) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createEmployee_asAdmin_shouldCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(employee1);
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is(employee1.getEmail())));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createEmployee_asAdmin_shouldReturnBadRequest_whenEmailExists() throws Exception {
        Employee newEmployee = new Employee("Test", "User", "john.doe@example.com", "11111", LocalDate.now(), "Tester", 50000.0);
        when(employeeService.createEmployee(any(Employee.class)))
                .thenThrow(new IllegalArgumentException("Employee with email " + newEmployee.getEmail() + " already exists."));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmployee)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Employee with email " + newEmployee.getEmail() + " already exists.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateEmployee_asAdmin_shouldUpdateEmployee() throws Exception {
        Employee updatedDetails = new Employee("John", "DoeUpdated", "john.doe@example.com", "54321", employee1.getHireDate(), "Senior Developer", 75000.0);
        updatedDetails.setId(1L); // Ensure ID is set for the update target

        when(employeeService.updateEmployee(eq(1L), any(Employee.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("DoeUpdated")))
                .andExpect(jsonPath("$.salary", is(75000.0)));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateEmployee_asAdmin_shouldReturnNotFound_whenEmployeeDoesNotExist() throws Exception {
        Employee updatedDetails = new Employee("Non", "Existent", "non.existent@example.com", "00000", LocalDate.now(), "Ghost", 0.0);
        when(employeeService.updateEmployee(eq(3L), any(Employee.class)))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: 3"));
        
        mockMvc.perform(put("/api/employees/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found with id: 3")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateEmployee_asAdmin_shouldReturnBadRequest_whenEmailTakenByAnother() throws Exception {
        Employee updatedDetails = new Employee("John", "Doe", "jane.smith@example.com", "12345", LocalDate.now(), "Developer", 70000.0);
        // Trying to update employee1 (ID 1L) with employee2's email
        when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
            .thenThrow(new IllegalArgumentException("Email " + updatedDetails.getEmail() + " is already in use by another employee."));

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Email " + updatedDetails.getEmail() + " is already in use by another employee.")));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteEmployee_asAdmin_shouldDeleteEmployee() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deleted", is(true)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteEmployee_asAdmin_shouldReturnNotFound_whenEmployeeDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Employee not found with id: 3")).when(employeeService).deleteEmployee(3L);
        mockMvc.perform(delete("/api/employees/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found with id: 3")))
                .andExpect(jsonPath("$.deleted", is(false)));
    }
}
