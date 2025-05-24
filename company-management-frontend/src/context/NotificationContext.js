import React, { createContext, useContext, useState, useCallback } from 'react';

const NotificationContext = createContext(null);

export const NotificationProvider = ({ children }) => {
    const [notification, setNotification] = useState(null); // { message, severity, open }

    const showNotification = useCallback((message, severity = 'info') => {
        setNotification({ message, severity, open: true });
    }, []);

    const hideNotification = useCallback(() => {
        setNotification(prev => prev ? { ...prev, open: false } : null);
    }, []);

    // Automatically hide after a delay, but allow manual close
    // This useEffect can be problematic if hideNotification changes reference too often.
    // Using a timeout directly in showNotification or in Notifier might be more stable.
    // For now, let's rely on Notifier's autoHideDuration and manual close.

    return (
        <NotificationContext.Provider value={{ notification, showNotification, hideNotification }}>
            {children}
        </NotificationContext.Provider>
    );
};

export const useNotification = () => {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error('useNotification must be used within a NotificationProvider');
    }
    return context;
};

export default NotificationContext;
