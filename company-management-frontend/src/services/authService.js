import axios from 'axios';

// Base URL for the authentication API. 
// TODO: Make this configurable via environment variables or a constants file.
const AUTH_API_URL = 'http://localhost:8080/api/auth';

/**
 * Logs in a user.
 * @param {string} username - The username.
 * @param {string} password - The password.
 * @returns {Promise<object>} The response data from the server, typically including a JWT.
 * @throws {Error} If login fails or network error occurs.
 */
export const login = async (username, password) => {
    try {
        const response = await axios.post(`${AUTH_API_URL}/login`, {
            username,
            password,
        });
        if (response.data && response.data.jwt) {
            localStorage.setItem('userToken', response.data.jwt);
            // Optionally, decode token here to get user info if needed, or store user details
            // if the backend returns them alongside the token.
        }
        return response.data; // Contains the JWT
    } catch (error) {
        // Axios wraps the error, error.response contains the server's response
        // if the server responded with an error code (4xx, 5xx).
        // If it's a network error or other issue, error.response might be undefined.
        console.error('Login failed:', error.response ? error.response.data : error.message);
        throw error.response ? new Error(error.response.data.message || 'Login failed') : error;
    }
};

/**
 * Logs out the current user.
 */
export const logout = () => {
    localStorage.removeItem('userToken');
    // Future: Add logic to redirect user or update global state (e.g., via context or Redux).
    // For example: window.location.href = '/login';
};

/**
 * Retrieves the JWT for the current user from localStorage.
 * @returns {string|null} The JWT string if available, otherwise null.
 */
export const getCurrentUserToken = () => {
    return localStorage.getItem('userToken');
};

/**
 * Checks if the current user is authenticated.
 * For now, this is a basic check for token existence.
 * TODO: Add token expiration check by decoding the token.
 * @returns {boolean} True if a token exists, false otherwise.
 */
export const isAuthenticated = () => {
    const token = getCurrentUserToken();
    return !!token; // Returns true if token is not null or empty, false otherwise.
};

/**
 * Registers a new user.
 * @param {object} userData - Object containing username, password, email, firstName, lastName.
 * @returns {Promise<object>} The response data from the server, typically the created user object.
 * @throws {Error} If registration fails or network error occurs.
 */
export const register = async (userData) => {
    try {
        const response = await axios.post(`${AUTH_API_URL}/register`, userData);
        return response.data; // Contains the registered user data (e.g., id, username, email, etc.)
    } catch (error) {
        console.error('Registration failed:', error.response ? error.response.data : error.message);
        if (error.response && error.response.data) {
            // Prefer a specific message from the backend if available
            if (error.response.data.message) {
                throw new Error(error.response.data.message);
            }
            // If backend sends structured validation errors (e.g., from MethodArgumentNotValidException)
            else if (error.response.data.errors && typeof error.response.data.errors === 'object') {
                const errorMessages = Object.entries(error.response.data.errors)
                    .map(([field, message]) => `${field}: ${message}`)
                    .join('; ');
                throw new Error(`Validation failed: ${errorMessages}`);
            }
            // Fallback for other server-side errors with a data payload
            else {
                throw new Error('Registration failed. Please check your input.');
            }
        } else if (error.request) {
            // The request was made but no response was received
            throw new Error('Registration failed. No response from server.');
        } else {
            // Something happened in setting up the request that triggered an Error
            throw new Error(`Registration failed: ${error.message}`);
        }
    }
};

// Optional: Function to get user details from token (example, not used yet)
/*
import { jwtDecode } from 'jwt-decode'; // You'd need to install jwt-decode: npm install jwt-decode

export const getUserDetailsFromToken = () => {
    const token = getCurrentUserToken();
    if (token) {
        try {
            const decoded = jwtDecode(token);
            return {
                username: decoded.sub, // Assuming 'sub' claim is username
                roles: decoded.roles ? decoded.roles.split(',') : [], // Assuming 'roles' claim is comma-separated
                // Add other relevant claims
            };
        } catch (error) {
            console.error("Invalid token:", error);
            logout(); // If token is invalid, log out user
            return null;
        }
    }
    return null;
};
*/

const authService = {
    login,
    logout,
    getCurrentUserToken,
    isAuthenticated,
    register, // Added register function
    // getUserDetailsFromToken, // Uncomment if you implement this
};

export default authService;
