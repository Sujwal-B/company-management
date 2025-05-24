package de.zeroco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Department;
import de.zeroco.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    private Department department1;
    private Department department2;

    @BeforeEach
    void setUp() {
        department1 = new Department("HR", "Building A");
        department1.setId(1L);

        department2 = new Department("Engineering", "Building B");
        department2.setId(2L);
    }

    // --- Unauthenticated Access Tests ---
    @Test
    void getAllDepartments_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createDepartment_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department1)))
                .andExpect(status().isUnauthorized());
    }

    // --- Authenticated Access Tests (ROLE_USER where applicable) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllDepartments_authenticated_shouldReturnListOfDepartments() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(List.of(department1, department2));
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(department1.getName())));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllDepartments_authenticated_shouldReturnEmptyList() throws Exception {
        when(departmentService.getAllDepartments()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getDepartmentById_authenticated_shouldReturnDepartment_whenFound() throws Exception {
        when(departmentService.getDepartmentById(1L)).thenReturn(department1);
        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(department1.getName())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getDepartmentById_authenticated_shouldReturnNotFound_whenNotFound() throws Exception {
        when(departmentService.getDepartmentById(3L)).thenThrow(new ResourceNotFoundException("Department not found with id: 3"));
        mockMvc.perform(get("/api/departments/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Department not found with id: 3")));
    }

    // --- Unauthorized Access Tests (ROLE_USER for ADMIN operations) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createDepartment_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void updateDepartment_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department1)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void deleteDepartment_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isForbidden());
    }

    // --- Admin Access Tests (ROLE_ADMIN) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createDepartment_asAdmin_shouldCreateDepartment() throws Exception {
        when(departmentService.createDepartment(any(Department.class))).thenReturn(department1);
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(department1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(department1.getName())));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createDepartment_asAdmin_shouldReturnBadRequest_whenNameExists() throws Exception {
        Department newDepartment = new Department("HR", "Building C");
        when(departmentService.createDepartment(any(Department.class)))
            .thenThrow(new IllegalArgumentException("Department with name '" + newDepartment.getName() + "' already exists."));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartment)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Department with name '" + newDepartment.getName() + "' already exists.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateDepartment_asAdmin_shouldUpdateDepartment() throws Exception {
        Department updatedDetails = new Department("Human Resources", "Building A - Renovated");
        updatedDetails.setId(1L);

        when(departmentService.updateDepartment(eq(1L), any(Department.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Human Resources")))
                .andExpect(jsonPath("$.location", is("Building A - Renovated")));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateDepartment_asAdmin_shouldReturnNotFound_whenDepartmentDoesNotExist() throws Exception {
        Department updatedDetails = new Department("NonExistent", "Location X");
        when(departmentService.updateDepartment(eq(3L), any(Department.class)))
                .thenThrow(new ResourceNotFoundException("Department not found with id: 3"));
        
        mockMvc.perform(put("/api/departments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Department not found with id: 3")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateDepartment_asAdmin_shouldReturnBadRequest_whenNameTakenByAnother() throws Exception {
        Department updatedDetails = new Department("Engineering", "Building C"); // Trying to take department2's name
        when(departmentService.updateDepartment(eq(1L), any(Department.class)))
            .thenThrow(new IllegalArgumentException("Department name '" + updatedDetails.getName() + "' is already in use by another department."));

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Department name '" + updatedDetails.getName() + "' is already in use by another department.")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteDepartment_asAdmin_shouldDeleteDepartment() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);
        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent()); // As per DepartmentController's successful delete response
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteDepartment_asAdmin_shouldReturnNotFound_whenDepartmentDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Department not found with id: 3")).when(departmentService).deleteDepartment(3L);
        mockMvc.perform(delete("/api/departments/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Department not found with id: 3")));
    }
}
