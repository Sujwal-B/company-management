package de.zeroco.controller;

import de.zeroco.exception.ResourceNotFoundException;
import de.zeroco.model.Department;
import de.zeroco.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Department Management", description = "APIs for managing departments")
@RestController
@RequestMapping("/api/departments")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Operation(summary = "Get all departments", description = "Retrieves a list of all departments. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of departments",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Page.class))), // Updated schema
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<Department> getAllDepartments(
            @Parameter(description = "Page number, 0-indexed", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by column", example = "id") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction", example = "asc") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return departmentService.getAllDepartments(pageable);
    }

    @Operation(summary = "Get a department by ID", description = "Retrieves a specific department by its ID. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved department",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Department.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Department not found",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDepartmentById(
            @Parameter(description = "ID of the department to be retrieved", required = true, example = "1")
            @PathVariable Long id) {
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(department);
        } catch (ResourceNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @Operation(summary = "Create a new department", description = "Creates a new department. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department created successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Department.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., duplicate name)",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have ADMIN role")
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> createDepartment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Department object that needs to be added",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Department.class))
            )
            @RequestBody Department department) {
        try {
            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Update an existing department", description = "Updates an existing department's details. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department updated successfully",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = Department.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input (e.g., name taken by another department)",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Department not found",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateDepartment(
            @Parameter(description = "ID of the department to be updated", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated department object",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Department.class))
            )
            @RequestBody Department departmentDetails) {
        try {
            Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
            return ResponseEntity.ok(updatedDepartment);
        } catch (ResourceNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (IllegalArgumentException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @Operation(summary = "Delete a department", description = "Deletes a department by its ID. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user does not have ADMIN role"),
            @ApiResponse(responseCode = "404", description = "Department not found",
                         content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Map.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteDepartment(
            @Parameter(description = "ID of the department to be deleted", required = true, example = "1")
            @PathVariable Long id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        } catch (ResourceNotFoundException ex) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
