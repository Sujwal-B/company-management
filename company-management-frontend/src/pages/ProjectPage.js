import React, { useState, useEffect, useCallback } from 'react';
import {
    Box, Button, Container, Typography, CircularProgress, /* Alert, */ // Alert removed
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import ProjectList from '../components/projects/ProjectList';
import ProjectForm from '../components/projects/ProjectForm';
import projectService from '../services/projectService';
import employeeService from '../services/employeeService';
import { useNotification } from '../context/NotificationContext'; // Import useNotification

const ProjectPage = () => {
    const [projects, setProjects] = useState([]);
    const [allEmployees, setAllEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    // const [error, setError] = useState(null); // Replaced
    const [openForm, setOpenForm] = useState(false);
    const [selectedProject, setSelectedProject] = useState(null);
    const [formSubmissionLoading, setFormSubmissionLoading] = useState(false);
    const { showNotification } = useNotification(); // Use notification hook

    // For delete confirmation
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [projectToDeleteId, setProjectToDeleteId] = useState(null);

    // Pagination state
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalProjects, setTotalProjects] = useState(0);

    const fetchProjectsAndEmployees = useCallback(async () => {
        setLoading(true);
        // setError(null); // Replaced
        try {
            const [projectsResponse, employeesResponse] = await Promise.all([
                projectService.getAllProjects(),
                employeeService.getAllEmployees()
            ]);
            setProjects(projectsResponse.data || []);
            setTotalProjects(projectsResponse.data ? projectsResponse.data.length : 0);
            setAllEmployees(employeesResponse.data || []);
        } catch (err) {
            console.error("Failed to fetch data:", err);
            const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch initial data.';
            // setError(errorMessage); // Replaced
            showNotification(errorMessage, 'error');
            setProjects([]);
            setAllEmployees([]);
            setTotalProjects(0);
        } finally {
            setLoading(false);
        }
    }, [showNotification]); // Added showNotification dependency

    useEffect(() => {
        fetchProjectsAndEmployees();
    }, [fetchProjectsAndEmployees]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleOpenForm = (project = null) => {
        setSelectedProject(project);
        setOpenForm(true);
        // setError(null); // Replaced
    };

    const handleCloseForm = () => {
        setOpenForm(false);
        setSelectedProject(null);
    };

    const handleFormSubmit = async (projectData) => {
        setFormSubmissionLoading(true);
        // setError(null); // Replaced
        try {
            if (selectedProject && selectedProject.id) {
                await projectService.updateProject(selectedProject.id, projectData);
                showNotification('Project updated successfully!', 'success');
            } else {
                await projectService.createProject(projectData);
                showNotification('Project created successfully!', 'success');
            }
            await fetchProjectsAndEmployees();
            handleCloseForm();
        } catch (err) {
            console.error("Failed to save project:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to save project.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to save project.', 'error');
        } finally {
            setFormSubmissionLoading(false);
        }
    };

    const handleOpenConfirmDialog = (id) => {
        setProjectToDeleteId(id);
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setProjectToDeleteId(null);
        setOpenConfirmDialog(false);
    };

    const handleDeleteProject = async () => {
        if (!projectToDeleteId) return;
        setFormSubmissionLoading(true);
        // setError(null); // Replaced
        try {
            await projectService.deleteProject(projectToDeleteId);
            showNotification('Project deleted successfully!', 'success');
            await fetchProjectsAndEmployees();
        } catch (err) {
            console.error("Failed to delete project:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to delete project.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to delete project.', 'error');
        } finally {
            setFormSubmissionLoading(false);
            handleCloseConfirmDialog();
        }
    };
    
    const handleManageEmployees = (project) => {
        handleOpenForm(project);
    };

    return (
        <Container maxWidth="xl" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h1">
                    Project Management
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => handleOpenForm()}
                >
                    Add Project
                </Button>
            </Box>

            {/* Local Alert component removed */}

            {loading && !projects.length ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <ProjectList
                    projects={projects}
                    onEdit={handleOpenForm}
                    onDelete={handleOpenConfirmDialog}
                    onManageEmployees={handleManageEmployees}
                    page={page}
                    rowsPerPage={rowsPerPage}
                    totalProjects={totalProjects}
                    handleChangePage={handleChangePage}
                    handleChangeRowsPerPage={handleChangeRowsPerPage}
                />
            )}

            <ProjectForm
                open={openForm}
                onClose={handleCloseForm}
                onSubmit={handleFormSubmit}
                initialData={selectedProject}
                isLoading={formSubmissionLoading}
                allEmployees={allEmployees}
            />

            <Dialog
                open={openConfirmDialog}
                onClose={handleCloseConfirmDialog}
            >
                <DialogTitle>Confirm Deletion</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this project? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmDialog} color="secondary" autoFocus disabled={formSubmissionLoading}>
                        Cancel
                    </Button>
                    <Button onClick={handleDeleteProject} color="error" disabled={formSubmissionLoading}>
                        {formSubmissionLoading ? <CircularProgress size={24} /> : "Delete"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default ProjectPage;
