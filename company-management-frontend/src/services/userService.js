import apiClient from './api'; // Assuming apiClient is the configured Axios instance
import authService from './authService'; // To potentially get username or roles

// Base URL for user profile related endpoints
const API_URL = '/users'; // Example: /api/users

/**
 * Fetches the profile of the currently logged-in user.
 * For now, this returns a mock user profile.
 * TODO: Replace with an actual API call when the backend endpoint is ready.
 *       e.g., return apiClient.get(`${API_URL}/me`);
 * @returns {Promise<object>} A promise that resolves to an object containing user profile data.
 */
export const getUserProfile = async () => {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 500));

    // Get username from token if possible, otherwise use a default
    let username = 'current_user';
    let roles = ['ROLE_USER'];

    // This is a placeholder. In a real app, user details might come from AuthContext,
    // which could decode them from the JWT. For now, we'll use the token to get basic info
    // if authService provides a way to decode it, or just use mock data.
    // Assuming authService.getUserDetailsFromToken() is implemented and returns { username, roles, ... }
    // const userDetails = authService.getUserDetailsFromToken(); 
    // if (userDetails) {
    //     username = userDetails.username;
    //     roles = userDetails.roles;
    // }
    
    // If you have a JWT decoding function in authService, you could use it:
    // const token = authService.getCurrentUserToken();
    // if (token) {
    // try {
    // const decoded = jwt_decode(token); // Assuming jwt_decode is available
    // username = decoded.sub || username;
    // roles = decoded.roles ? decoded.roles.split(',') : roles;
    // } catch (e) { console.error("Failed to decode token for profile", e); }
    // }


    return Promise.resolve({
        data: {
            id: 1, // Mock ID
            username: username, 
            firstName: 'John',   // Mock data
            lastName: 'Doe',     // Mock data
            email: `${username.replace(/\s+/g, '.').toLowerCase()}@example.com`, // Mock email based on username
            roles: roles,        // Mock roles
            // Add other fields as expected from backend, e.g., hireDate, department, etc.
        }
    });
};

/**
 * Updates the password for the currently logged-in user.
 * @param {object} passwordData - Object containing { currentPassword, newPassword }.
 * @returns {Promise<object>} A promise that resolves to the response from the server.
 * TODO: Replace with an actual API call when backend endpoint is ready.
 *       e.g., return apiClient.post(`${API_URL}/me/change-password`, passwordData);
 */
export const updatePassword = async (passwordData) => {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 1000));

    console.log("Simulating password change with data:", passwordData);

    // Mock responses based on a simple validation
    if (!passwordData.currentPassword || !passwordData.newPassword) {
        return Promise.reject({ 
            response: { 
                data: { message: "Current and new passwords are required." },
                status: 400 
            } 
        });
    }
    if (passwordData.currentPassword === "password123") { // Mock "correct" current password
        if(passwordData.newPassword === passwordData.currentPassword){
            return Promise.reject({ response: { data: { message: "New password cannot be the same as the old password." }, status: 400 } });
        }
        return Promise.resolve({ 
            data: { message: "Password updated successfully!" } 
        });
    } else {
        return Promise.reject({ 
            response: { 
                data: { message: "Incorrect current password." },
                status: 400 // Or 401/403 depending on backend
            } 
        });
    }
};

const userService = {
    getUserProfile,
    updatePassword,
};

export default userService;
