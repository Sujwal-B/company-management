import React, { useState, useEffect, useCallback } from 'react';
import {
    Box, Button, Container, Typography, CircularProgress, /* Alert, */ // Alert removed
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import EmployeeList from '../components/employees/EmployeeList';
import EmployeeForm from '../components/employees/EmployeeForm';
import employeeService from '../services/employeeService';
import { useNotification } from '../context/NotificationContext'; // Import useNotification

const EmployeePage = () => {
    const [employees, setEmployees] = useState([]);
    const [loading, setLoading] = useState(false);
    // const [error, setError] = useState(null); // Replaced by global notification
    const [openForm, setOpenForm] = useState(false);
    const [selectedEmployee, setSelectedEmployee] = useState(null);
    const [formSubmissionLoading, setFormSubmissionLoading] = useState(false);
    const { showNotification } = useNotification(); // Use notification hook

    // For delete confirmation
    const [openConfirmDialog, setOpenConfirmDialog] = useState(false);
    const [employeeToDeleteId, setEmployeeToDeleteId] = useState(null);
    
    // Pagination state
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [totalEmployees, setTotalEmployees] = useState(0);


    const fetchEmployees = useCallback(async () => {
        setLoading(true);
        // setError(null); // No longer needed
        try {
            const response = await employeeService.getAllEmployees();
            setEmployees(response.data || []);
            setTotalEmployees(response.data ? response.data.length : 0); 
        } catch (err) {
            console.error("Failed to fetch employees:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to fetch employees.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to fetch employees.', 'error');
        } finally {
            setLoading(false);
        }
    }, [showNotification]); // Added showNotification to dependencies

    useEffect(() => {
        fetchEmployees();
    }, [fetchEmployees]);
    
    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };


    const handleOpenForm = (employee = null) => {
        setSelectedEmployee(employee);
        setOpenForm(true);
        // setError(null); // Clear previous form errors - no longer needed for local error state
    };

    const handleCloseForm = () => {
        setOpenForm(false);
        setSelectedEmployee(null);
    };

    const handleFormSubmit = async (employeeData) => {
        setFormSubmissionLoading(true);
        // setError(null); // No longer needed
        try {
            if (selectedEmployee && selectedEmployee.id) {
                await employeeService.updateEmployee(selectedEmployee.id, employeeData);
                showNotification('Employee updated successfully!', 'success');
            } else {
                await employeeService.createEmployee(employeeData);
                showNotification('Employee created successfully!', 'success');
            }
            await fetchEmployees(); 
            handleCloseForm();
        } catch (err) {
            console.error("Failed to save employee:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to save employee.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to save employee.', 'error');
        } finally {
            setFormSubmissionLoading(false);
        }
    };

    const handleOpenConfirmDialog = (id) => {
        setEmployeeToDeleteId(id);
        setOpenConfirmDialog(true);
    };

    const handleCloseConfirmDialog = () => {
        setEmployeeToDeleteId(null);
        setOpenConfirmDialog(false);
    };

    const handleDeleteEmployee = async () => {
        if (!employeeToDeleteId) return;
        // Using formSubmissionLoading for delete button as well to disable it
        setFormSubmissionLoading(true); 
        // setError(null); // No longer needed
        try {
            await employeeService.deleteEmployee(employeeToDeleteId);
            showNotification('Employee deleted successfully!', 'success');
            await fetchEmployees(); 
        } catch (err) {
            console.error("Failed to delete employee:", err);
            // setError(err.response?.data?.message || err.message || 'Failed to delete employee.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to delete employee.', 'error');
        } finally {
            setFormSubmissionLoading(false);
            handleCloseConfirmDialog();
        }
    };


    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Typography variant="h4" component="h1">
                    Employee Management
                </Typography>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<AddIcon />}
                    onClick={() => handleOpenForm()}
                >
                    Add Employee
                </Button>
            </Box>

            {/* Local Alert component removed */}

            {loading && !employees.length ? ( 
                <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
                    <CircularProgress />
                </Box>
            ) : (
                <EmployeeList
                    employees={employees}
                    onEdit={handleOpenForm}
                    onDelete={handleOpenConfirmDialog}
                    page={page}
                    rowsPerPage={rowsPerPage}
                    totalEmployees={totalEmployees}
                    handleChangePage={handleChangePage}
                    handleChangeRowsPerPage={handleChangeRowsPerPage}
                />
            )}

            <EmployeeForm
                open={openForm}
                onClose={handleCloseForm}
                onSubmit={handleFormSubmit}
                initialData={selectedEmployee}
                isLoading={formSubmissionLoading}
            />

            <Dialog
                open={openConfirmDialog}
                onClose={handleCloseConfirmDialog}
                aria-labelledby="alert-dialog-title"
                aria-describedby="alert-dialog-description"
            >
                <DialogTitle id="alert-dialog-title">{"Confirm Deletion"}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                        Are you sure you want to delete this employee? This action cannot be undone.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmDialog} color="secondary" autoFocus disabled={formSubmissionLoading}>
                        Cancel
                    </Button>
                    <Button onClick={handleDeleteEmployee} color="error" disabled={formSubmissionLoading}>
                        {formSubmissionLoading ? <CircularProgress size={24} /> : "Delete"}
                    </Button>
                </DialogActions>
            </Dialog>

        </Container>
    );
};

export default EmployeePage;
