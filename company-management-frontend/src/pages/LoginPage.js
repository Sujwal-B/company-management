import React, { useState } from 'react';
import { useNavigate, Link as RouterLink } from 'react-router-dom'; // Added RouterLink
import { useAuth } from '../context/AuthContext';
import { useNotification } from '../context/NotificationContext'; // Import useNotification
import authService from '../services/authService';
import { Container, TextField, Button, Typography, Box, Paper, CircularProgress, Link } from '@mui/material'; // Removed Alert, Added Link

const LoginPage = ({ openRegisterModal }) => { // Added openRegisterModal prop
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    // const [error, setError] = useState(''); // Replaced by global notification
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { login: contextLogin } = useAuth();
    const { showNotification } = useNotification(); // Get showNotification

    const handleSubmit = async (event) => {
        event.preventDefault();
        // setError(''); // No longer needed
        setLoading(true);

        if (!username || !password) {
            // setError('Both username and password are required.'); // Replaced
            showNotification('Both username and password are required.', 'warning');
            setLoading(false);
            return;
        }

        try {
            const response = await authService.login(username, password);
            if (response && response.jwt) {
                contextLogin(response.jwt);
                navigate('/dashboard');
                showNotification('Login successful!', 'success'); // Success notification
            } else {
                // setError('Login failed: No token received.'); // Replaced
                showNotification('Login failed: No token received.', 'error');
            }
        } catch (err) {
            // setError(err.message || 'Login failed. Please check your credentials.'); // Replaced
            showNotification(err.message || 'Login failed. Please check your credentials.', 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <Paper 
                elevation={3} 
                sx={{ 
                    padding: 4,
                    marginTop: 8, 
                    display: 'flex', 
                    flexDirection: 'column', 
                    alignItems: 'center' 
                }}
            >
                <Typography component="h1" variant="h5">
                    Company Portal Sign In
                </Typography>
                {/* Error Alert removed, handled by global Notifier */}
                <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1, width: '100%' }}>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="username"
                        label="Username"
                        name="username"
                        autoComplete="username"
                        autoFocus
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        disabled={loading} // Disable when loading
                    />
                    <TextField
                        variant="outlined" // Explicitly set variant
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Password"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        disabled={loading} // Disable when loading
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ 
                            mt: 3, 
                            mb: 2,
                            transition: 'filter 0.15s ease-in-out, box-shadow 0.15s ease-in-out',
                            '&:hover': {
                                filter: 'brightness(0.9)',
                                boxShadow: (theme) => theme.shadows[4], // Increase shadow on hover
                            }
                        }}
                        disabled={loading} // Disable button when loading
                    >
                        {loading ? <CircularProgress size={24} color="inherit" /> : "Sign In"}
                    </Button>
                    <Box textAlign="center" sx={{ mt: 2 }}>
                        <Link component="button" variant="body2" onClick={openRegisterModal} type="button">
                            Don't have an account? Sign Up
                        </Link>
                    </Box>
                </Box>
            </Paper>
        </Container>
    );
};

export default LoginPage;
