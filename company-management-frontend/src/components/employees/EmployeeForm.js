import React, { useState, useEffect } from 'react';
import {
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    TextField, Button, Grid, CircularProgress
} from '@mui/material';

const initialFormState = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    hireDate: '', // Should be YYYY-MM-DD for input type="date"
    jobTitle: '',
    salary: '',
};

const EmployeeForm = ({ open, onClose, onSubmit, initialData, isLoading }) => {
    const [formState, setFormState] = useState(initialFormState);

    useEffect(() => {
        if (initialData) {
            setFormState({
                firstName: initialData.firstName || '',
                lastName: initialData.lastName || '',
                email: initialData.email || '',
                phoneNumber: initialData.phoneNumber || '',
                // Ensure hireDate is in 'yyyy-MM-dd' format if it's a Date object or ISO string
                hireDate: initialData.hireDate ? new Date(initialData.hireDate).toISOString().split('T')[0] : '',
                jobTitle: initialData.jobTitle || '',
                salary: initialData.salary || '',
            });
        } else {
            setFormState(initialFormState);
        }
    }, [initialData, open]); // Reset form when 'open' changes or 'initialData' changes

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormState(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleFormSubmit = (event) => {
        event.preventDefault();
        // Convert salary to number if it's not empty
        const payload = {
            ...formState,
            salary: formState.salary ? parseFloat(formState.salary) : null,
            // Ensure hireDate is sent in a format backend expects, if it's just date, it's fine
        };
        onSubmit(payload);
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>{initialData ? 'Edit Employee' : 'Add New Employee'}</DialogTitle>
            <DialogContent>
                <DialogContentText sx={{ mb: 2 }}>
                    {initialData ? 'Update the details of the employee.' : 'Please fill in the details for the new employee.'}
                </DialogContentText>
                <Box component="form" onSubmit={handleFormSubmit} noValidate>
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="firstName"
                                label="First Name"
                                value={formState.firstName}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="lastName"
                                label="Last Name"
                                value={formState.lastName}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                name="email"
                                label="Email Address"
                                type="email"
                                value={formState.email}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="phoneNumber"
                                label="Phone Number"
                                value={formState.phoneNumber}
                                onChange={handleChange}
                                fullWidth
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="hireDate"
                                label="Hire Date"
                                type="date"
                                value={formState.hireDate}
                                onChange={handleChange}
                                fullWidth
                                required
                                InputLabelProps={{ shrink: true }}
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="jobTitle"
                                label="Job Title"
                                value={formState.jobTitle}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="salary"
                                label="Salary"
                                type="number"
                                value={formState.salary}
                                onChange={handleChange}
                                fullWidth
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                    </Grid>
                </Box>
            </DialogContent>
            <DialogActions sx={{ p: '16px 24px' }}>
                <Button onClick={onClose} color="secondary" disabled={isLoading}>
                    Cancel
                </Button>
                <Button onClick={handleFormSubmit} variant="contained" color="primary" disabled={isLoading}>
                    {isLoading ? <CircularProgress size={24} color="inherit" /> : (initialData ? 'Save Changes' : 'Create Employee')}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default EmployeeForm;
