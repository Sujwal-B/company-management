package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.model.Project;
import de.zeroco.repository.EmployeeRepository;
import de.zeroco.repository.ProjectRepository;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project project1;
    private Project project2;
    private Employee employee1;

    @BeforeEach
    void setUp() {
        project1 = new Project("Alpha Project", "Description for Alpha", LocalDate.now(), LocalDate.now().plusMonths(6));
        project1.setId(1L);

        project2 = new Project("Beta Project", "Description for Beta", LocalDate.now(), LocalDate.now().plusMonths(3));
        project2.setId(2L);

        employee1 = new Employee("Test", "User", "test.user@example.com", "123", LocalDate.now(), "Tester", 50000.0);
        employee1.setId(101L);
    }

    @Test
    void getAllProjects_shouldReturnListOfProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        List<Project> projects = projectService.getAllProjects();
        assertEquals(2, projects.size());
        verify(projectRepository).findAll();
    }

    @Test
    void getAllProjects_shouldReturnEmptyList() {
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());
        List<Project> projects = projectService.getAllProjects();
        assertTrue(projects.isEmpty());
        verify(projectRepository).findAll();
    }

    @Test
    void getProjectById_shouldReturnProject_whenFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        Project found = projectService.getProjectById(1L);
        assertNotNull(found);
        assertEquals("Alpha Project", found.getName());
        verify(projectRepository).findById(1L);
    }

    @Test
    void getProjectById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(projectRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(3L));
        verify(projectRepository).findById(3L);
    }

    @Test
    void createProject_shouldSaveAndReturnProject_whenNameNotExists() {
        when(projectRepository.findByName(project1.getName())).thenReturn(Optional.empty());
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project created = projectService.createProject(project1); // project1 has an empty employees set by default
        assertNotNull(created);
        assertEquals(project1.getName(), created.getName());
        assertTrue(created.getEmployees().isEmpty()); // Check that employees set is empty as per service logic
        verify(projectRepository).findByName(project1.getName());
        verify(projectRepository).save(project1);
    }

    @Test
    void createProject_shouldThrowIllegalArgumentException_whenNameExists() {
        Project newProject = new Project("Alpha Project", "Another Alpha", null, null); // Duplicate name
        when(projectRepository.findByName("Alpha Project")).thenReturn(Optional.of(project1));

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(newProject));
        verify(projectRepository).findByName("Alpha Project");
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void updateProject_shouldUpdateAndReturnProject_whenFoundAndNameNotChangedOrAvailable() {
        Project updatedDetails = new Project("Alpha Project", "Updated Description", project1.getStartDate(), project1.getEndDate());
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.findByName("Alpha Project")).thenReturn(Optional.of(project1)); // Name not changed
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project updated = projectService.updateProject(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Updated Description", updated.getDescription());
        verify(projectRepository).findById(1L);
        verify(projectRepository).save(any(Project.class));
    }
    
    @Test
    void updateProject_shouldUpdateAndReturnProject_whenNameChangedAndAvailable() {
        Project updatedDetails = new Project("Alpha Project New Name", "Description for Alpha", LocalDate.now(), LocalDate.now().plusMonths(6));
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1)); // Original name "Alpha Project"
        when(projectRepository.findByName("Alpha Project New Name")).thenReturn(Optional.empty()); // New name is available
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project updated = projectService.updateProject(1L, updatedDetails);
        
        assertEquals("Alpha Project New Name", updated.getName());
        verify(projectRepository).findById(1L);
        verify(projectRepository).findByName("Alpha Project New Name");
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void updateProject_shouldThrowResourceNotFoundException_whenNotFound() {
        Project updatedDetails = new Project("NonExistent", "Desc", null, null);
        when(projectRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(3L, updatedDetails));
        verify(projectRepository).findById(3L);
        verify(projectRepository, never()).save(any(Project.class));
    }
    
    @Test
    void updateProject_shouldThrowIllegalArgumentException_whenNameChangedAndTakenByAnother() {
        Project updatedDetails = new Project("Beta Project", "New description for Alpha trying to take Beta's name", null, null);
        // project1 (ID 1) is being updated to take project2's name (ID 2, Name "Beta Project")
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.findByName("Beta Project")).thenReturn(Optional.of(project2)); // project2 already has this name

        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(1L, updatedDetails));
        verify(projectRepository).findById(1L);
        verify(projectRepository).findByName("Beta Project");
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void deleteProject_shouldDeleteProject_whenFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        doNothing().when(projectRepository).delete(project1);

        projectService.deleteProject(1L);

        verify(projectRepository).findById(1L);
        verify(projectRepository).delete(project1);
    }

    @Test
    void deleteProject_shouldThrowResourceNotFoundException_whenNotFound() {
        when(projectRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(3L));
        verify(projectRepository).findById(3L);
        verify(projectRepository, never()).delete(any(Project.class));
    }

    // Tests for assignEmployeeToProject and removeEmployeeFromProject
    @Test
    void assignEmployeeToProject_shouldAddEmployeeAndSaveProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(employeeRepository.findById(101L)).thenReturn(Optional.of(employee1));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project updatedProject = projectService.assignEmployeeToProject(1L, 101L);

        assertTrue(updatedProject.getEmployees().contains(employee1));
        assertTrue(employee1.getProjectsInternal().contains(project1)); // Check bidirectional link via internal getter
        verify(projectRepository).findById(1L);
        verify(employeeRepository).findById(101L);
        verify(projectRepository).save(project1);
    }

    @Test
    void assignEmployeeToProject_shouldThrowResourceNotFoundException_whenProjectNotFound() {
        when(projectRepository.findById(3L)).thenReturn(Optional.empty());
        // employeeRepository.findById won't be called if project not found first
        
        assertThrows(ResourceNotFoundException.class, () -> projectService.assignEmployeeToProject(3L, 101L));
        verify(projectRepository).findById(3L);
        verify(employeeRepository, never()).findById(anyLong());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void assignEmployeeToProject_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.assignEmployeeToProject(1L, 999L));
        verify(projectRepository).findById(1L);
        verify(employeeRepository).findById(999L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void removeEmployeeFromProject_shouldRemoveEmployeeAndSaveProject() {
        // Setup: employee1 is already in project1
        project1.getEmployees().add(employee1); // Use internal access for setup
        employee1.getProjectsInternal().add(project1);  // Use internal access for setup

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(employeeRepository.findById(101L)).thenReturn(Optional.of(employee1));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);

        Project updatedProject = projectService.removeEmployeeFromProject(1L, 101L);

        assertFalse(updatedProject.getEmployees().contains(employee1));
        assertFalse(employee1.getProjectsInternal().contains(project1)); // Check bidirectional link
        verify(projectRepository).findById(1L);
        verify(employeeRepository).findById(101L);
        verify(projectRepository).save(project1);
    }
    
    @Test
    void removeEmployeeFromProject_shouldThrowResourceNotFoundException_whenProjectNotFound() {
        when(projectRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.removeEmployeeFromProject(3L, 101L));
        verify(projectRepository).findById(3L);
        verify(employeeRepository, never()).findById(anyLong());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void removeEmployeeFromProject_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.removeEmployeeFromProject(1L, 999L));
        verify(projectRepository).findById(1L);
        verify(employeeRepository).findById(999L);
        verify(projectRepository, never()).save(any(Project.class));
    }
}
