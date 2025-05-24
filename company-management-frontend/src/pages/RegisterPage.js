import React, { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom';
import {
    Container,
    Paper,
    Typography,
    Box,
    TextField,
    Button,
    CircularProgress,
    Link
} from '@mui/material';
import { useNotification } from '../context/NotificationContext';
// Assuming authService is correctly set up for registration
import authService from '../services/authService';
// Optional: import { useAuth } from '../context/AuthContext';

const RegisterPage = () => {
    const navigate = useNavigate();
    const { showNotification } = useNotification();
    // Optional: const { login: autoLogin } = useAuth(); // If auto-login after registration is desired

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

        // Client-side Validation
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
            const { confirmPassword, ...registrationData } = formData; // Exclude confirmPassword from data sent to backend
            await authService.register(registrationData);
            showNotification('Registration successful! Please log in.', 'success');
            navigate('/login');
            // Optional: Auto-login logic
            // try {
            //   await autoLogin(registrationData.username, registrationData.password);
            //   navigate('/'); // Navigate to dashboard or home
            // } catch (loginErr) {
            //   showNotification('Registration successful, but auto-login failed. Please log in manually.', 'warning');
            //   navigate('/login');
            // }
        } catch (err) {
            showNotification(err.message || 'Registration failed. Please try again.', 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Paper elevation={3} sx={{ marginTop: 8, padding: 4, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography component="h1" variant="h5">
                    Sign Up
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3, width: '100%' }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={formData.username}
                        onChange={handleChange}
                        disabled={loading}
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
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={loading}
                    >
                        {loading ? <CircularProgress size={24} color="inherit" /> : 'Sign Up'}
                    </Button>
                    <Box textAlign="center">
                        <Link component={RouterLink} to="/login" variant="body2">
                            Already have an account? Sign In
                        </Link>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
};

export default RegisterPage;
