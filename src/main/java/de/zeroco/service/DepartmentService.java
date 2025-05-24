package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Department;
import de.zeroco.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Transactional
    public Department createDepartment(Department department) {
        Optional<Department> existingDepartmentByName = departmentRepository.findByName(department.getName());
        if (existingDepartmentByName.isPresent()) {
            throw new IllegalArgumentException("Department with name '" + department.getName() + "' already exists.");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department existingDepartment = getDepartmentById(id); // Throws ResourceNotFoundException if not found

        // Check if the new name is different and if it already exists for another department
        if (!existingDepartment.getName().equals(departmentDetails.getName())) {
            Optional<Department> departmentByNewName = departmentRepository.findByName(departmentDetails.getName());
            if (departmentByNewName.isPresent() && !departmentByNewName.get().getId().equals(existingDepartment.getId())) {
                throw new IllegalArgumentException("Department name '" + departmentDetails.getName() + "' is already in use by another department.");
            }
            existingDepartment.setName(departmentDetails.getName());
        }

        existingDepartment.setLocation(departmentDetails.getLocation());

        return departmentRepository.save(existingDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department existingDepartment = getDepartmentById(id); // Throws ResourceNotFoundException if not found
        departmentRepository.delete(existingDepartment);
    }
}
