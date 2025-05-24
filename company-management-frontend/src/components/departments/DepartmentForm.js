import React, { useState, useEffect } from 'react';
import {
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    TextField, Button, Grid, CircularProgress, Box
} from '@mui/material';

const initialFormState = {
    name: '',
    location: '',
};

const DepartmentForm = ({ open, onClose, onSubmit, initialData, isLoading }) => {
    const [formState, setFormState] = useState(initialFormState);

    useEffect(() => {
        if (initialData) {
            setFormState({
                name: initialData.name || '',
                location: initialData.location || '',
            });
        } else {
            setFormState(initialFormState);
        }
    }, [initialData, open]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormState(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleFormSubmit = (event) => {
        event.preventDefault();
        onSubmit(formState);
    };

    return (
        <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
            <DialogTitle>{initialData ? 'Edit Department' : 'Add New Department'}</DialogTitle>
            <Box component="form" onSubmit={handleFormSubmit} noValidate>
                <DialogContent>
                    <DialogContentText sx={{ mb: 2 }}>
                        {initialData ? 'Update the details of the department.' : 'Please fill in the details for the new department.'}
                    </DialogContentText>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField
                                name="name"
                                label="Department Name"
                                value={formState.name}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                                autoFocus
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                name="location"
                                label="Location"
                                value={formState.location}
                                onChange={handleChange}
                                fullWidth
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                    </Grid>
                </DialogContent>
                <DialogActions sx={{ p: '16px 24px' }}>
                    <Button onClick={onClose} color="secondary" disabled={isLoading}>
                        Cancel
                    </Button>
                    <Button type="submit" variant="contained" color="primary" disabled={isLoading}>
                        {isLoading ? <CircularProgress size={24} color="inherit" /> : (initialData ? 'Save Changes' : 'Create Department')}
                    </Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
};

export default DepartmentForm;
