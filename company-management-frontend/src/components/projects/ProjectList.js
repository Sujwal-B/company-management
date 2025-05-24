import React from 'react';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
    IconButton, Typography, Box, TablePagination, Tooltip, Chip
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import GroupAddIcon from '@mui/icons-material/GroupAdd'; // Icon for Manage Employees

const ProjectList = ({ projects, onEdit, onDelete, onManageEmployees, page, rowsPerPage, totalProjects, handleChangePage, handleChangeRowsPerPage }) => {
    
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        // Ensure the date is valid before trying to format
        if (isNaN(date.getTime())) return 'Invalid Date';
        return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
    };

    if (!projects || projects.length === 0) {
        return (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="subtitle1">No projects found.</Typography>
            </Paper>
        );
    }

    return (
        <Paper sx={{ width: '100%', overflow: 'hidden' }}>
            <TableContainer sx={{ maxHeight: 650 }}> {/* Adjust maxHeight as needed */}
                <Table stickyHeader aria-label="project table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 'bold', minWidth: 170 }}>Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', minWidth: 200 }}>Description</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', minWidth: 120 }}>Start Date</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', minWidth: 120 }}>End Date</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', minWidth: 150 }}>Assigned Employees</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', textAlign: 'center', minWidth: 180 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {projects.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((project) => (
                            <TableRow hover key={project.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell component="th" scope="row">
                                    {project.name}
                                </TableCell>
                                <TableCell>
                                    <Typography variant="body2" noWrap sx={{ maxWidth: 300, overflow: 'hidden', textOverflow: 'ellipsis' }} title={project.description}>
                                        {project.description || 'N/A'}
                                    </Typography>
                                </TableCell>
                                <TableCell>{formatDate(project.startDate)}</TableCell>
                                <TableCell>{formatDate(project.endDate)}</TableCell>
                                <TableCell>
                                    {project.employees && project.employees.length > 0 ? (
                                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5, maxHeight: '50px', overflowY: 'auto' }}>
                                            {project.employees.slice(0, 2).map(emp => ( // Show first 2 employees as chips
                                                <Chip key={emp.id} label={`${emp.firstName} ${emp.lastName}`} size="small" />
                                            ))}
                                            {project.employees.length > 2 && (
                                                <Tooltip title={project.employees.slice(2).map(emp => `${emp.firstName} ${emp.lastName}`).join(', ')}>
                                                    <Chip label={`+${project.employees.length - 2} more`} size="small" />
                                                </Tooltip>
                                            )}
                                        </Box>
                                    ) : (
                                        <Typography variant="body2" color="text.secondary">None</Typography>
                                    )}
                                </TableCell>
                                <TableCell align="center">
                                    <Tooltip title="Edit Project Details & Assignments">
                                        <IconButton 
                                            onClick={() => onEdit(project)} 
                                            color="primary"
                                            aria-label={`edit project ${project.name}`}
                                        >
                                            <EditIcon />
                                        </IconButton>
                                    </Tooltip>
                                     {/* "Manage Employees" button can re-use the edit form or open a dedicated one */}
                                    <Tooltip title="Manage Assigned Employees (same as Edit for now)">
                                        <IconButton
                                            onClick={() => onManageEmployees(project)}
                                            color="info" // Using 'info' color for distinction
                                            aria-label={`manage employees for project ${project.name}`}
                                        >
                                            <GroupAddIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Delete Project">
                                        <IconButton 
                                            onClick={() => onDelete(project.id)} 
                                            color="error"
                                            aria-label={`delete project ${project.name}`}
                                        >
                                            <DeleteIcon />
                                        </IconButton>
                                    </Tooltip>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <TablePagination
                rowsPerPageOptions={[5, 10, 15, 25]}
                component="div"
                count={totalProjects}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </Paper>
    );
};

export default ProjectList;
