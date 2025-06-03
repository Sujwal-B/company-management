import React, { useState, useEffect, useCallback } from 'react';
import {
    Box, Button, Container, Typography, CircularProgress, /* Alert, */ // Alert removed
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DepartmentList from '../components/departments/DepartmentList';
import DepartmentForm from '../components/departments/DepartmentForm';
import departmentService from '../services/departmentService';
import { useNotification } from '../context/NotificationContext'; // Import useNotification

const DepartmentPage = () => {
    const [departments, setDepartments] = useState([]);
    const [loading, setLoading] = useState(false);
    // const [error, setError] = useState(null); // Replaced
    const [openForm, setOpenForm] = useState(false);
    const [selectedDepartment, setSelectedDepartment] = useState(null);
    const [formSubmissionLoading, setFormSubmissionLoading] = useState(false);
    const { showNotification } = useNotification(); // Use notification hook

    // For delete confirmation
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [departmentToDeleteId, setDepartmentToDeleteId] = useState(null);

    // Pagination state
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalDepartments, setTotalDepartments] = useState(0);

    const fetchDepartments = useCallback(async (currentPage, currentRowsPerPage) => {
        setLoading(true);
        try {
            const response = await departmentService.getAllDepartments(currentPage, currentRowsPerPage);
            setDepartments(response.data.content || []);
            setTotalDepartments(response.data.totalElements || 0);
        } catch (err) {
            console.error("Failed to fetch departments:", err);
            showNotification(err.response?.data?.message || err.message || 'Failed to fetch departments.', 'error');
            setDepartments([]);
            setTotalDepartments(0);
        } finally {
            setLoading(false);
        }
    }, [showNotification]);

    useEffect(() => {
        fetchDepartments(page, rowsPerPage);
    }, [fetchDepartments, page, rowsPerPage]);

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0); 
    };

    const handleOpenForm = (department = null) => {
        setSelectedDepartment(department);
        setOpenForm(true);
        // setError(null); // Replaced
    };

    const handleCloseForm = () => {
        setOpenForm(false);
        setSelectedDepartment(null);
    };

    const handleFormSubmit = async (departmentData) => {
        setFormSubmissionLoading(true);
        // setError(null); // Replaced
        try {
            if (selectedDepartment && selectedDepartment.id) {
                await departmentService.updateDepartment(selectedDepartment.id, departmentData);
                showNotification('Department updated successfully!', 'success');
            } else {
                await departmentService.createDepartment(departmentData);
                showNotification('Department created successfully!', 'success');
            }
            fetchDepartments(page, rowsPerPage); // Refetch current page
            handleCloseForm();
        } catch (err) {
            console.error("Failed to save department:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to save department.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to save department.', 'error');
        } finally {
            setFormSubmissionLoading(false);
        }
    };

    const handleOpenConfirmDialog = (id) => {
        setDepartmentToDeleteId(id);
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setDepartmentToDeleteId(null);
        setOpenConfirmDialog(false);
    };

    const handleDeleteDepartment = async () => {
        if (!departmentToDeleteId) return;
        setFormSubmissionLoading(true); 
        // setError(null); // Replaced
        try {
            await departmentService.deleteDepartment(departmentToDeleteId);
            showNotification('Department deleted successfully!', 'success');
            fetchDepartments(page, rowsPerPage); // Refetch current page
        } catch (err) {
            console.error("Failed to delete department:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to delete department.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to delete department.', 'error');
        } finally {
            setFormSubmissionLoading(false);
            handleCloseConfirmDialog();
        }
    };

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h1">
                    Department Management
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => handleOpenForm()}
                >
                    Add Department
                </Button>
            </Box>

            {/* Local Alert component removed */}

            {loading && !departments.length ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <DepartmentList
                    departments={departments}
                    onEdit={handleOpenForm}
                    onDelete={handleOpenConfirmDialog}
                    page={page}
                    rowsPerPage={rowsPerPage}
                    totalDepartments={totalDepartments}
                    handleChangePage={handleChangePage}
                    handleChangeRowsPerPage={handleChangeRowsPerPage}
                />
            )}

            <DepartmentForm
                open={openForm}
                onClose={handleCloseForm}
                onSubmit={handleFormSubmit}
                initialData={selectedDepartment}
                isLoading={formSubmissionLoading}
            />

            <Dialog
                open={openConfirmDialog}
                onClose={handleCloseConfirmDialog}
            >
                <DialogTitle>Confirm Deletion</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to delete this department? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmDialog} color="secondary" autoFocus disabled={formSubmissionLoading}>
                        Cancel
                    </Button>
                    <Button onClick={handleDeleteDepartment} color="error" disabled={formSubmissionLoading}>
                        {formSubmissionLoading ? <CircularProgress size={24} /> : "Delete"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default DepartmentPage;
