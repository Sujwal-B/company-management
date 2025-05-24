import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService'; // Assuming authService is in ../services

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null); // Can store token or decoded user info

    useEffect(() => {
        // Initialize auth state from authService (e.g., check localStorage)
        const authenticated = authService.isAuthenticated();
        setIsAuthenticated(authenticated);
        if (authenticated) {
            // Optionally, set user based on token (e.g., decode it or just store the token)
            // For now, just indicating that a user session exists by setting a simple object
            setUser({ token: authService.getCurrentUserToken() });
        }
    }, []);

    const login = (token) => {
        // The token is already stored in localStorage by authService.login
        setIsAuthenticated(true);
        setUser({ token }); // Store the token or decoded user info
        // No need to call authService.login again here, as it's called from LoginPage
    };

    const logout = () => {
        authService.logout(); // Clears token from localStorage
        setIsAuthenticated(false);
        setUser(null);
        // Optionally, redirect to login page: window.location.href = '/login';
        // However, routing should ideally be handled by components/navigation hooks.
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthContext;
