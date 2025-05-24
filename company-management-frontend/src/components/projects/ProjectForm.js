import React, { useState, useEffect } from 'react';
import {
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    TextField, Button, Grid, CircularProgress, Box,
    Select, MenuItem, InputLabel, FormControl, OutlinedInput, Chip
} from '@mui/material';

const initialFormState = {
    name: '',
    description: '',
    startDate: '', // YYYY-MM-DD
    endDate: '',   // YYYY-MM-DD
    employeeIds: [], // Store IDs of selected employees
};

// Helper for Select styling
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
        },
    },
};

const ProjectForm = ({ open, onClose, onSubmit, initialData, isLoading, allEmployees = [] }) => {
    const [formState, setFormState] = useState(initialFormState);
    const [selectedEmployeeIds, setSelectedEmployeeIds] = useState([]);

    useEffect(() => {
        if (initialData) {
            setFormState({
                name: initialData.name || '',
                description: initialData.description || '',
                startDate: initialData.startDate ? new Date(initialData.startDate).toISOString().split('T')[0] : '',
                endDate: initialData.endDate ? new Date(initialData.endDate).toISOString().split('T')[0] : '',
                // employeeIds are handled by selectedEmployeeIds state
            });
            // Pre-select employees if initialData has them
            setSelectedEmployeeIds(initialData.employees ? initialData.employees.map(emp => emp.id) : []);
        } else {
            setFormState(initialFormState);
            setSelectedEmployeeIds([]);
        }
    }, [initialData, open]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormState(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleEmployeeSelectChange = (event) => {
        const { target: { value } } = event;
        setSelectedEmployeeIds(
            // On autofill, event.target.value may be a stringified value.
            typeof value === 'string' ? value.split(',') : value,
        );
    };

    const handleFormSubmit = (event) => {
        event.preventDefault();
        // Construct payload. Backend might expect a list of employee IDs or full employee objects.
        // Assuming backend can handle a list of employee IDs or objects with just ID.
        const payload = {
            ...formState,
            // Pass employees as a list of objects with just IDs, or just IDs, depending on backend DTO
            // For simplicity, let's assume backend DTO for Project can take a list of Employee objects (with only IDs populated)
            // or that the service layer on backend handles fetching full Employee entities if only IDs are passed.
            // A common approach is to send only IDs:
            employees: selectedEmployeeIds.map(id => ({ id })), 
            // Or if backend expects just IDs: employeeIds: selectedEmployeeIds 
        };
        onSubmit(payload);
    };
    
    // Get employee names for the chips in Select
    const getSelectedEmployeeNames = () => {
        return allEmployees
            .filter(emp => selectedEmployeeIds.includes(emp.id))
            .map(emp => `${emp.firstName} ${emp.lastName}`)
            .join(', ');
    };


    return (
        <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
            <DialogTitle>{initialData ? 'Edit Project' : 'Add New Project'}</DialogTitle>
            <Box component="form" onSubmit={handleFormSubmit} noValidate>
                <DialogContent>
                    <DialogContentText sx={{ mb: 2 }}>
                        {initialData ? 'Update the project details and assigned employees.' : 'Fill in the details for the new project and assign employees.'}
                    </DialogContentText>
                    <Grid container spacing={3}>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="name"
                                label="Project Name"
                                value={formState.name}
                                onChange={handleChange}
                                fullWidth
                                required
                                margin="dense"
                                disabled={isLoading}
                                autoFocus
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                             <FormControl fullWidth margin="dense" disabled={isLoading}>
                                <InputLabel id="project-employees-select-label">Assigned Employees</InputLabel>
                                <Select
                                    labelId="project-employees-select-label"
                                    id="project-employees-select"
                                    multiple
                                    value={selectedEmployeeIds}
                                    onChange={handleEmployeeSelectChange}
                                    input={<OutlinedInput id="select-multiple-chip" label="Assigned Employees" />}
                                    renderValue={(selected) => (
                                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                                            {allEmployees.filter(emp => selected.includes(emp.id)).map((emp) => (
                                                <Chip key={emp.id} label={`${emp.firstName} ${emp.lastName}`} size="small" />
                                            ))}
                                        </Box>
                                    )}
                                    MenuProps={MenuProps}
                                >
                                    {allEmployees.map((employee) => (
                                        <MenuItem
                                            key={employee.id}
                                            value={employee.id}
                                        >
                                            {`${employee.firstName} ${employee.lastName} (${employee.email})`}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                name="description"
                                label="Description"
                                value={formState.description}
                                onChange={handleChange}
                                fullWidth
                                multiline
                                rows={4}
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="startDate"
                                label="Start Date"
                                type="date"
                                value={formState.startDate}
                                onChange={handleChange}
                                fullWidth
                                InputLabelProps={{ shrink: true }}
                                margin="dense"
                                disabled={isLoading}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                name="endDate"
                                label="End Date"
                                type="date"
                                value={formState.endDate}
                                onChange={handleChange}
                                fullWidth
                                InputLabelProps={{ shrink: true }}
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
                        {isLoading ? <CircularProgress size={24} color="inherit" /> : (initialData ? 'Save Changes' : 'Create Project')}
                    </Button>
                </DialogActions>
            </Box>
        </Dialog>
    );
};

export default ProjectForm;
