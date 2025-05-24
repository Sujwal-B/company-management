package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Department;
import de.zeroco.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
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

    @Test
    void getAllDepartments_shouldReturnListOfDepartments() {
        when(departmentRepository.findAll()).thenReturn(List.of(department1, department2));
        List<Department> departments = departmentService.getAllDepartments();
        assertEquals(2, departments.size());
        verify(departmentRepository).findAll();
    }

    @Test
    void getAllDepartments_shouldReturnEmptyList() {
        when(departmentRepository.findAll()).thenReturn(Collections.emptyList());
        List<Department> departments = departmentService.getAllDepartments();
        assertTrue(departments.isEmpty());
        verify(departmentRepository).findAll();
    }

    @Test
    void getDepartmentById_shouldReturnDepartment_whenFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        Department found = departmentService.getDepartmentById(1L);
        assertNotNull(found);
        assertEquals("HR", found.getName());
        verify(departmentRepository).findById(1L);
    }

    @Test
    void getDepartmentById_shouldThrowResourceNotFoundException_whenNotFound() {
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> departmentService.getDepartmentById(3L));
        verify(departmentRepository).findById(3L);
    }

    @Test
    void createDepartment_shouldSaveAndReturnDepartment_whenNameNotExists() {
        when(departmentRepository.findByName(department1.getName())).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(department1);

        Department created = departmentService.createDepartment(department1);
        assertNotNull(created);
        assertEquals(department1.getName(), created.getName());
        verify(departmentRepository).findByName(department1.getName());
        verify(departmentRepository).save(department1);
    }

    @Test
    void createDepartment_shouldThrowIllegalArgumentException_whenNameExists() {
        Department newDepartment = new Department("HR", "Building C"); // Duplicate name
        when(departmentRepository.findByName("HR")).thenReturn(Optional.of(department1));

        assertThrows(IllegalArgumentException.class, () -> departmentService.createDepartment(newDepartment));
        verify(departmentRepository).findByName("HR");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_shouldUpdateAndReturnDepartment_whenFoundAndNameNotChangedOrAvailable() {
        Department updatedDetails = new Department("HR", "Building A - Updated");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        // Assuming name is not changed or if changed, it's available (or it's the same department's name)
        when(departmentRepository.findByName("HR")).thenReturn(Optional.of(department1)); 
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Department updated = departmentService.updateDepartment(1L, updatedDetails);

        assertNotNull(updated);
        assertEquals("Building A - Updated", updated.getLocation());
        verify(departmentRepository).findById(1L);
        verify(departmentRepository).save(any(Department.class));
    }
    
    @Test
    void updateDepartment_shouldUpdateAndReturnDepartment_whenNameChangedAndAvailable() {
        Department updatedDetails = new Department("Human Resources Updated", "Building A");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1)); // Original name "HR"
        when(departmentRepository.findByName("Human Resources Updated")).thenReturn(Optional.empty()); // New name is available
        when(departmentRepository.save(any(Department.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Department updated = departmentService.updateDepartment(1L, updatedDetails);

        assertEquals("Human Resources Updated", updated.getName());
        verify(departmentRepository).findById(1L);
        verify(departmentRepository).findByName("Human Resources Updated");
        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void updateDepartment_shouldThrowResourceNotFoundException_whenNotFound() {
        Department updatedDetails = new Department("NonExistent", "Location X");
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> departmentService.updateDepartment(3L, updatedDetails));
        verify(departmentRepository).findById(3L);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_shouldThrowIllegalArgumentException_whenNameChangedAndTakenByAnother() {
        // department1 (ID 1, Name "HR") is being updated to take department2's name (ID 2, Name "Engineering")
        Department updatedDetails = new Department("Engineering", "Building C");
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        when(departmentRepository.findByName("Engineering")).thenReturn(Optional.of(department2)); // department2 already has this name

        assertThrows(IllegalArgumentException.class, () -> departmentService.updateDepartment(1L, updatedDetails));
        verify(departmentRepository).findById(1L);
        verify(departmentRepository).findByName("Engineering");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void deleteDepartment_shouldDeleteDepartment_whenFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department1));
        doNothing().when(departmentRepository).delete(department1);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).findById(1L);
        verify(departmentRepository).delete(department1);
    }

    @Test
    void deleteDepartment_shouldThrowResourceNotFoundException_whenNotFound() {
        when(departmentRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> departmentService.deleteDepartment(3L));
        verify(departmentRepository).findById(3L);
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
