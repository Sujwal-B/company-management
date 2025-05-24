import axios from 'axios';
import { getCurrentUserToken } from './authService'; // Import from authService

// Base URL for the backend APIs.
// TODO: Make this configurable via environment variables.
const API_BASE_URL = 'http://localhost:8080/api'; // Base for all non-auth API endpoints

// Create an Axios instance with default configuration
const apiClient = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add the JWT token to headers
apiClient.interceptors.request.use(
    (config) => {
        const token = getCurrentUserToken();
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        // Handle request errors here
        return Promise.reject(error);
    }
);

// Optional: Response interceptor for global error handling or token refresh logic
apiClient.interceptors.response.use(
    (response) => {
        // Any status code that lie within the range of 2xx cause this function to trigger
        return response;
    },
    (error) => {
        // Any status codes that falls outside the range of 2xx cause this function to trigger
        // For example, handle 401 Unauthorized globally by logging out the user
        if (error.response && error.response.status === 401) {
            // If not an auth error on login page already
            if (window.location.pathname !== '/login') { // Avoid logout loops if /login itself returns 401
                // Consider importing logout directly or using an event emitter if authService is too coupled
                // For now, this direct call might be problematic if api.js is imported by authService.js (circular dependency).
                // A better approach would be to handle this in a component or use an event bus.
                // For simplicity, let's assume this is handled at a higher level for now or authService.logout() is safe to call.
                // import { logout } from './authService'; // This would create circular dependency if authService imports apiClient
                // logout(); 
                // window.location.href = '/login';
                console.error("Unauthorized request or token expired. Consider redirecting to login.");
            }
        }
        return Promise.reject(error);
    }
);

export default apiClient;
