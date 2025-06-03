import React from 'react';
import {
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
    IconButton, Typography, Box, TablePagination, Tooltip
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';

const DepartmentList = ({ departments, onEdit, onDelete, page, rowsPerPage, totalDepartments, handleChangePage, handleChangeRowsPerPage }) => {
    
    if (!departments || departments.length === 0) {
        return (
            <Paper sx={{ p: 3, textAlign: 'center' }}>
                <Typography variant="subtitle1">No departments found.</Typography>
            </Paper>
        );
    }

    return (
        <Paper sx={{ width: '100%', overflow: 'hidden' }}>
            <TableContainer sx={{ maxHeight: 600 }}> {/* Adjust maxHeight as needed */}
                <Table stickyHeader aria-label="department table">
                    <TableHead>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 'bold' }}>Name</TableCell>
                            <TableCell sx={{ fontWeight: 'bold' }}>Location</TableCell>
                            <TableCell sx={{ fontWeight: 'bold', textAlign: 'center' }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {departments.map((department) => (
                            <TableRow hover key={department.id} sx={{ '&:last-child td, &:last-child th': { border: 0 } }}>
                                <TableCell component="th" scope="row">
                                    {department.name}
                                </TableCell>
                                <TableCell>{department.location || 'N/A'}</TableCell>
                                <TableCell align="center">
                                    <Tooltip title="Edit Department">
                                        <IconButton 
                                            onClick={() => onEdit(department)} 
                                            color="primary"
                                            aria-label={`edit department ${department.name}`}
                                        >
                                            <EditIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Tooltip title="Delete Department">
                                        <IconButton 
                                            onClick={() => onDelete(department.id)} 
                                            color="error"
                                            aria-label={`delete department ${department.name}`}
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
                count={totalDepartments}
                rowsPerPage={rowsPerPage}
                page={page}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
            />
        </Paper>
    );
};

export default DepartmentList;
