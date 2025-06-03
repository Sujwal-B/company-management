import React from 'react';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
    IconButton, Typography, Box, TablePagination, Tooltip
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const EmployeeList = ({ employees, onEdit, onDelete, page, rowsPerPage, totalEmployees, handleChangePage, handleChangeRowsPerPage }) => {
    
    const formatDate = (dateString) => {
        if (!dateString) return 'N/A';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
    };

    if (!employees || employees.length === 0) {
        return (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="subtitle1">No employees found.</Typography>
            </Paper>
        );
    }

    return (
        <Paper sx={{ width: '100%', overflow: 'hidden' }}>
            <TableContainer sx={{ maxHeight: 600 }}> {/* Adjust maxHeight as needed */}
                <Table stickyHeader aria-label="employee table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 'bold' }}>Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Email</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Phone</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Job Title</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Hire Date</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {employees.map((employee) => (
                            <TableRow hover key={employee.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell component="th" scope="row">
                                    {`${employee.firstName} ${employee.lastName}`}
                                </TableCell>
                                <TableCell>{employee.email}</TableCell>
                                <TableCell>{employee.phoneNumber || 'N/A'}</TableCell>
                                <TableCell>{employee.jobTitle}</TableCell>
                                <TableCell>{formatDate(employee.hireDate)}</TableCell>
                                <TableCell align="center">
                                    <Tooltip title="Edit Employee">
                                        <IconButton 
                                            onClick={() => onEdit(employee)} 
                                            color="primary"
                                            aria-label={`edit employee ${employee.firstName} ${employee.lastName}`}
                                        >
                                            <EditIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Delete Employee">
                                        <IconButton 
                                            onClick={() => onDelete(employee.id)} 
                                            color="error"
                                            aria-label={`delete employee ${employee.firstName} ${employee.lastName}`}
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
                rowsPerPageOptions={[5, 10, 25, 50]}
                component="div"
                count={totalEmployees} // Use totalEmployees from props
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </Paper>
    );
};

export default EmployeeList;
