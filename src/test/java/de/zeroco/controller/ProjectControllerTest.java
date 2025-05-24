package de.zeroco.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.model.Project;
import de.zeroco.service.ProjectService;
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
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private Project project1;
    private Project project2;
    private Employee employee1;

    @BeforeEach
    void setUp() {
        project1 = new Project("Alpha Project", "Desc Alpha", LocalDate.now(), LocalDate.now().plusMonths(6));
        project1.setId(1L);

        project2 = new Project("Beta Project", "Desc Beta", LocalDate.now(), LocalDate.now().plusMonths(3));
        project2.setId(2L);
        
        employee1 = new Employee("Test", "User", "test.user@example.com", null, null, null, null);
        employee1.setId(101L);
    }

    // --- Unauthenticated Access Tests ---
    @Test
    void getAllProjects_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createProject_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void assignEmployeeToProject_unauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/projects/1/employees/101"))
                .andExpect(status().isUnauthorized());
    }


    // --- Authenticated Access Tests (ROLE_USER where applicable) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllProjects_authenticated_shouldReturnListOfProjects() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of(project1, project2));
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(project1.getName())));
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getAllProjects_authenticated_shouldReturnEmptyList() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getProjectById_authenticated_shouldReturnProject_whenFound() throws Exception {
        when(projectService.getProjectById(1L)).thenReturn(project1);
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(project1.getName())));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getProjectById_authenticated_shouldReturnNotFound_whenNotFound() throws Exception {
        when(projectService.getProjectById(3L)).thenThrow(new ResourceNotFoundException("Project not found with id: 3"));
        mockMvc.perform(get("/api/projects/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found with id: 3")));
    }

    // --- Unauthorized Access Tests (ROLE_USER for ADMIN operations) ---
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void createProject_authorizedAsUser_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void assignEmployeeToProject_authorizedAsUser_shouldReturnForbidden() throws Exception {
         mockMvc.perform(post("/api/projects/1/employees/101"))
                .andExpect(status().isForbidden());
    }

    // --- Admin Access Tests (ROLE_ADMIN) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProject_asAdmin_shouldCreateProject() throws Exception {
        when(projectService.createProject(any(Project.class))).thenReturn(project1);
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(project1.getName())));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createProject_asAdmin_shouldReturnBadRequest_whenNameExists() throws Exception {
        Project newProject = new Project("Alpha Project", "Desc", null, null);
        when(projectService.createProject(any(Project.class)))
            .thenThrow(new IllegalArgumentException("Project with name '" + newProject.getName() + "' already exists."));

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProject)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Project with name '" + newProject.getName() + "' already exists.")));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProject_asAdmin_shouldUpdateProject() throws Exception {
        Project updatedDetails = new Project("Alpha Project Updated", "New Desc", project1.getStartDate(), project1.getEndDate());
        updatedDetails.setId(1L);

        when(projectService.updateProject(eq(1L), any(Project.class))).thenReturn(updatedDetails);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alpha Project Updated")));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateProject_asAdmin_shouldReturnNotFound_whenProjectDoesNotExist() throws Exception {
        Project updatedDetails = new Project("NonExistent", "Desc", null, null);
        when(projectService.updateProject(eq(3L), any(Project.class)))
                .thenThrow(new ResourceNotFoundException("Project not found with id: 3"));
        
        mockMvc.perform(put("/api/projects/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found with id: 3")));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProject_asAdmin_shouldDeleteProject() throws Exception {
        doNothing().when(projectService).deleteProject(1L);
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteProject_asAdmin_shouldReturnNotFound_whenProjectDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Project not found with id: 3")).when(projectService).deleteProject(3L);
        mockMvc.perform(delete("/api/projects/3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found with id: 3")));
    }

    // --- Employee Assignment/Removal Tests (ROLE_ADMIN) ---
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void assignEmployeeToProject_asAdmin_shouldAssignEmployee() throws Exception {
        // Clone project1 to avoid modifying the shared instance directly in this test's mock setup
        Project projectWithEmployee = new Project(project1.getName(), project1.getDescription(), project1.getStartDate(), project1.getEndDate());
        projectWithEmployee.setId(project1.getId());
        projectWithEmployee.setEmployees(Set.of(employee1)); // Simulate the state after employee is added

        when(projectService.assignEmployeeToProject(1L, 101L)).thenReturn(projectWithEmployee);

        mockMvc.perform(post("/api/projects/1/employees/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project1.getName())))
                .andExpect(jsonPath("$.employees[0].id", is(101))); // Check if employee is in the returned project
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void assignEmployeeToProject_asAdmin_shouldReturnNotFound_whenProjectNotFound() throws Exception {
        when(projectService.assignEmployeeToProject(3L, 101L))
                .thenThrow(new ResourceNotFoundException("Project not found with id: 3"));
        mockMvc.perform(post("/api/projects/3/employees/101"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found with id: 3")));
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void assignEmployeeToProject_asAdmin_shouldReturnNotFound_whenEmployeeNotFound() throws Exception {
        when(projectService.assignEmployeeToProject(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: 999"));
        mockMvc.perform(post("/api/projects/1/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found with id: 999")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeEmployeeFromProject_asAdmin_shouldRemoveEmployee() throws Exception {
         // project1 initially has no employees in this test's setup
        when(projectService.removeEmployeeFromProject(1L, 101L)).thenReturn(project1); 

        mockMvc.perform(delete("/api/projects/1/employees/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(project1.getName())))
                .andExpect(jsonPath("$.employees", hasSize(0))); // Check employee is removed
    }
    
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void removeEmployeeFromProject_asAdmin_shouldReturnNotFound_whenProjectNotFound() throws Exception {
        when(projectService.removeEmployeeFromProject(3L, 101L))
                .thenThrow(new ResourceNotFoundException("Project not found with id: 3"));
        mockMvc.perform(delete("/api/projects/3/employees/101"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Project not found with id: 3")));
    }
}
