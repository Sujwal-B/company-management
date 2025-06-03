package de.zeroco.service;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Employee;
import de.zeroco.model.Project;
import de.zeroco.repository.EmployeeRepository;
import de.zeroco.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository; // For assigning/removing employees

    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Project> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));
    }

    @Transactional
    public Project createProject(Project project) {
        Optional<Project> existingProjectByName = projectRepository.findByName(project.getName());
        if (existingProjectByName.isPresent()) {
            throw new IllegalArgumentException("Project with name '" + project.getName() + "' already exists.");
        }
        // Ensure employees set is handled correctly if provided at creation (usually empty or handled by assignment methods)
        if (project.getEmployees() != null && !project.getEmployees().isEmpty()) {
            // This might indicate a need to fetch and attach managed Employee entities
            // For simplicity, let's assume projects are created without employees initially,
            // and employees are assigned via assignEmployeeToProject.
            // If employees are to be created with the project, ensure they are managed entities.
            project.getEmployees().clear(); // Or handle appropriately
        }
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(Long id, Project projectDetails) {
        Project existingProject = getProjectById(id); // Throws ResourceNotFoundException if not found

        // Check if the new name is different and if it already exists for another project
        if (!existingProject.getName().equals(projectDetails.getName())) {
            Optional<Project> projectByNewName = projectRepository.findByName(projectDetails.getName());
            if (projectByNewName.isPresent() && !projectByNewName.get().getId().equals(existingProject.getId())) {
                throw new IllegalArgumentException("Project name '" + projectDetails.getName() + "' is already in use by another project.");
            }
            existingProject.setName(projectDetails.getName());
        }

        existingProject.setDescription(projectDetails.getDescription());
        existingProject.setStartDate(projectDetails.getStartDate());
        existingProject.setEndDate(projectDetails.getEndDate());
        // Note: Managing employee assignments (adding/removing employees) should typically be done
        // through specific service methods like assignEmployeeToProject / removeEmployeeFromProject
        // rather than directly setting the employees set here, to ensure data integrity and proper handling
        // of the relationship. If projectDetails.getEmployees() is passed, it might contain detached entities.

        return projectRepository.save(existingProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project projectToDelete = getProjectById(id); // Throws ResourceNotFoundException if not found
        // JPA will handle the removal of entries from the join table (project_employee)
        // due to the @ManyToMany relationship definition.
        // If there are other explicit lifecycle requirements (e.g., unassigning employees manually first),
        // they would be handled here. For now, direct deletion is fine.
        projectRepository.delete(projectToDelete);
    }

    @Transactional
    public Project assignEmployeeToProject(Long projectId, Long employeeId) {
        Project project = getProjectById(projectId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        project.addEmployee(employee); // This uses the helper method in Project entity
        return projectRepository.save(project);
    }

    @Transactional
    public Project removeEmployeeFromProject(Long projectId, Long employeeId) {
        Project project = getProjectById(projectId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        project.removeEmployee(employee); // This uses the helper method in Project entity
        return projectRepository.save(project);
    }
}
