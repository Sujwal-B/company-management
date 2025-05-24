import React, { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
    Modal,
    Container,
    Paper,
    Typography,
    Box,
    TextField,
    Button,
    CircularProgress,
    Link,
    IconButton
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import { useNotification } from '../../context/NotificationContext';
import authService from '../../services/authService';

const RegisterModal = ({ open, onClose }) => {
    const navigate = useNavigate();
    const { showNotification } = useNotification();

    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        firstName: '',
        lastName: '',
    });
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        for (const key in formData) {
            if (formData[key] === '') {
                showNotification('All fields are required.', 'warning');
                setLoading(false);
                return;
            }
        }

        if (formData.password !== formData.confirmPassword) {
            showNotification('Passwords do not match.', 'warning');
            setLoading(false);
            return;
        }

        try {
            const { confirmPassword, ...registrationData } = formData;
            await authService.register(registrationData);
            showNotification('Registration successful! Please log in.', 'success');
            onClose(); // Close modal on success
            navigate('/login'); // Redirect to login page
        } catch (err) {
            showNotification(err.message || 'Registration failed. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    // Reset form when modal is closed/opened
    React.useEffect(() => {
        if (!open) {
            setFormData({
                username: '',
                email: '',
                password: '',
                confirmPassword: '',
                firstName: '',
                lastName: '',
            });
            setLoading(false);
        }
    }, [open]);


    return (
        <Modal
            open={open}
            onClose={onClose}
            aria-labelledby="register-modal-title"
            sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }} // Center modal
        >
            <Box maxWidth="xs" sx={{outline: 'none', width: '100%', p: { xs: 2, sm: 3 } }}> {/* Changed Container to Box, added responsive padding */}
                <Paper 
                    elevation={6} // Slightly more elevation for modal
                    sx={{ 
                        padding: 3, // Adjusted padding
                        display: 'flex', 
                        flexDirection: 'column', 
                        alignItems: 'center',
                        position: 'relative', // For close button positioning
                    }}
                >
                    <IconButton
                        aria-label="close"
                        onClick={onClose}
                        sx={{
                            position: 'absolute',
                            right: 8,
                            top: 8,
                            color: (theme) => theme.palette.grey[500],
                        }}
                    >
                        <CloseIcon />
                    </IconButton>
                    <Typography component="h1" variant="h5" id="register-modal-title">
                        Sign Up
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 1, width: '100%' }}> {/* Consistent top margin with LoginPage */}
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="username"
                            label="Username"
                            name="username"
                            autoComplete="username"
                            autoFocus // Autofocus on the first field
                            value={formData.username}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="email"
                            label="Email Address"
                            name="email"
                            type="email"
                            autoComplete="email"
                            value={formData.email}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="firstName"
                            label="First Name"
                            name="firstName"
                            autoComplete="given-name"
                            value={formData.firstName}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="lastName"
                            label="Last Name"
                            name="lastName"
                            autoComplete="family-name"
                            value={formData.lastName}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                            autoComplete="new-password"
                            value={formData.password}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="confirmPassword"
                            label="Confirm Password"
                            type="password"
                            id="confirmPassword"
                            autoComplete="new-password"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            disabled={loading}
                            variant="outlined"
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ 
                                mt: 3, // Consistent button margin with LoginPage
                                mb: 2,
                                transition: 'filter 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
                                '&:hover': {
                                    filter: 'brightness(0.9)',
                                    boxShadow: (theme) => theme.shadows[4],
                                }
                            }}
                            disabled={loading}
                        >
                            {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign Up'}
                        </Button>
                        <Box textAlign="center">
                            <Link component={RouterLink} to="/login" variant="body2" onClick={onClose}> {/* Close modal when clicking this link */}
                                Already have an account? Sign In
                            </Link>
                        </Box>
                    </Box>
                </Paper>
            </Box>
        </Modal>
    );
};

export default RegisterModal;
