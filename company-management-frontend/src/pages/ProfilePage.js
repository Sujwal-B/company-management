import React, { useState, useEffect, useCallback } from 'react';
import {
    Box, Container, Typography, /* Grid, Card, CardContent, */ TextField, Button,
    CircularProgress, /* Alert, */ Paper, /* Divider, */ List, ListItem, ListItemText, ListItemIcon
} from '@mui/material'; // Removed unused Grid, Card, CardContent, Alert, Divider
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import EmailIcon from '@mui/icons-material/Email';
import VpnKeyIcon from '@mui/icons-material/VpnKey';
import PersonIcon from '@mui/icons-material/Person';
import LockResetIcon from '@mui/icons-material/LockReset';

import userService from '../services/userService';
// import { useAuth } from '../context/AuthContext'; // Not strictly needed if profile comes from userService
import { useNotification } from '../context/NotificationContext'; // Import useNotification

const ProfilePage = () => {
    const [profile, setProfile] = useState(null);
    const [loadingProfile, setLoadingProfile] = useState(true);
    // const [errorProfile, setErrorProfile] = useState(''); // Replaced

    const [currentPassword, setCurrentPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmNewPassword, setConfirmNewPassword] = useState('');
    const [loadingPasswordChange, setLoadingPasswordChange] = useState(false);
    // const [errorPasswordChange, setErrorPasswordChange] = useState(''); // Replaced
    // const [successPasswordChange, setSuccessPasswordChange] = useState(''); // Replaced
    const { showNotification } = useNotification(); // Use notification hook
    // const { user: authUser } = useAuth(); // Example if needed for username source

    const fetchUserProfile = useCallback(async () => {
        setLoadingProfile(true);
        // setErrorProfile(''); // Replaced
        try {
            const response = await userService.getUserProfile();
            setProfile(response.data);
        } catch (err) {
            console.error("Failed to fetch profile:", err);
            // setErrorProfile(err.response?.data?.message || err.message || 'Failed to fetch profile data.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to fetch profile data.', 'error');
        } finally {
            setLoadingProfile(false);
        }
    }, [showNotification]); // Added showNotification dependency

    useEffect(() => {
        fetchUserProfile();
    }, [fetchUserProfile]);

    const handlePasswordChangeSubmit = async (event) => {
        event.preventDefault();
        // setErrorPasswordChange(''); // Replaced
        // setSuccessPasswordChange(''); // Replaced

        if (!currentPassword || !newPassword || !confirmNewPassword) {
            // setErrorPasswordChange('All password fields are required.'); // Replaced
            showNotification('All password fields are required.', 'warning');
            return;
        }
        if (newPassword !== confirmNewPassword) {
            // setErrorPasswordChange('New password and confirmation password do not match.'); // Replaced
            showNotification('New password and confirmation password do not match.', 'warning');
            return;
        }
        if (newPassword.length < 6) {
            // setErrorPasswordChange('New password must be at least 6 characters long.'); // Replaced
            showNotification('New password must be at least 6 characters long.', 'warning');
            return;
        }

        setLoadingPasswordChange(true);
        try {
            const response = await userService.updatePassword({ currentPassword, newPassword });
            // setSuccessPasswordChange(response.data.message || 'Password updated successfully!'); // Replaced
            showNotification(response.data.message || 'Password updated successfully!', 'success');
            setCurrentPassword('');
            setNewPassword('');
            setConfirmNewPassword('');
        } catch (err) {
            console.error("Failed to update password:", err);
            // setErrorPasswordChange(err.response?.data?.message || err.message || 'Failed to update password.'); // Replaced
            showNotification(err.response?.data?.message || err.message || 'Failed to update password.', 'error');
        } finally {
            setLoadingPasswordChange(false);
        }
    };

    if (loadingProfile && !profile) { // Show loading only if profile is not yet fetched
        return (
            <Container sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
                <CircularProgress />
            </Container>
        );
    }

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" component="h1" gutterBottom>
                User Profile
            </Typography>

            {/* errorProfile Alert removed */}

            {profile ? ( // Render profile only if not null
                <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
                    <Typography variant="h5" component="h2" gutterBottom>
                        Profile Information
                    </Typography>
                    <List>
                        <ListItem>
                            <ListItemIcon><AccountCircleIcon color="primary" /></ListItemIcon>
                            <ListItemText primary="Username" secondary={profile.username} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><PersonIcon color="primary" /></ListItemIcon>
                            <ListItemText primary="Full Name" secondary={`${profile.firstName || ''} ${profile.lastName || ''}`} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><EmailIcon color="primary" /></ListItemIcon>
                            <ListItemText primary="Email" secondary={profile.email} />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><VpnKeyIcon color="primary" /></ListItemIcon>
                            <ListItemText primary="Roles" secondary={profile.roles?.join(', ') || 'N/A'} />
                        </ListItem>
                    </List>
                </Paper>
            ) : (
                !loadingProfile && <Typography>Could not load profile information.</Typography> // Show if profile is null and not loading
            )}

            <Paper elevation={3} sx={{ p: 3 }}>
                <Typography variant="h5" component="h2" gutterBottom>
                    Change Password
                </Typography>
                <Box component="form" onSubmit={handlePasswordChangeSubmit} noValidate sx={{ mt: 1 }}>
                    {/* errorPasswordChange and successPasswordChange Alerts removed */}
                    
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="currentPassword"
                        label="Current Password"
                        type="password"
                        id="currentPassword"
                        autoComplete="current-password"
                        value={currentPassword}
                        onChange={(e) => setCurrentPassword(e.target.value)}
                        disabled={loadingPasswordChange}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="newPassword"
                        label="New Password"
                        type="password"
                        id="newPassword"
                        autoComplete="new-password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        disabled={loadingPasswordChange}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="confirmNewPassword"
                        label="Confirm New Password"
                        type="password"
                        id="confirmNewPassword"
                        autoComplete="new-password"
                        value={confirmNewPassword}
                        onChange={(e) => setConfirmNewPassword(e.target.value)}
                        disabled={loadingPasswordChange}
                    />
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        color="primary"
                        sx={{ mt: 3, mb: 2 }}
                        disabled={loadingPasswordChange}
                        startIcon={loadingPasswordChange ? null : <LockResetIcon />}
                    >
                        {loadingPasswordChange ? <CircularProgress size={24} color="inherit" /> : "Change Password"}
                    </Button>
                </Box>
            </Paper>
        </Container>
    );
};

export default ProfilePage;
