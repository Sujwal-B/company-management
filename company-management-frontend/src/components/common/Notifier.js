import React from 'react';
import { Snackbar, Alert } from '@mui/material';
import { useNotification } from '../../context/NotificationContext'; // Corrected path

const Notifier = () => {
    const { notification, hideNotification } = useNotification();

    if (!notification || !notification.open) {
        return null;
    }

    const handleClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        hideNotification();
    };

    return (
        <Snackbar
            open={notification.open}
            autoHideDuration={6000} // Standard duration for snackbars
            onClose={handleClose}
            anchorOrigin={{ vertical: 'top', horizontal: 'center' }} // Or 'bottom', 'left'/'right'
        >
            <Alert
                onClose={handleClose} // Provide close button on the Alert itself
                severity={notification.severity || 'info'} // Default to 'info' if not specified
                variant="filled" // Use filled variant for better visibility
                sx={{ width: '100%' }}
            >
                {notification.message}
            </Alert>
        </Snackbar>
    );
};

export default Notifier;
