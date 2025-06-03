import apiClient from './api'; // Assuming apiClient is the configured Axios instance

const API_URL = '/departments'; // Base URL for department endpoints relative to apiClient's baseURL

/**
 * Fetches all departments with pagination.
 * @param {number} page - The page number (0-indexed).
 * @param {number} size - The number of items per page.
 * @param {string} sortBy - The column to sort by (optional).
 * @param {string} sortDir - The sort direction ('asc' or 'desc') (optional).
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const getAllDepartments = (page = 0, size = 10, sortBy = 'id', sortDir = 'asc') => {
    return apiClient.get(API_URL, {
        params: {
            page,
            size,
            sortBy,
            sortDir
        }
    });
};

/**
 * Fetches a single department by its ID.
 * @param {number|string} id - The ID of the department.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const getDepartmentById = (id) => {
    return apiClient.get(`${API_URL}/${id}`);
};

/**
 * Creates a new department.
 * @param {object} departmentData - The data for the new department.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const createDepartment = (departmentData) => {
    return apiClient.post(API_URL, departmentData);
};

/**
 * Updates an existing department.
 * @param {number|string} id - The ID of the department to update.
 * @param {object} departmentData - The updated data for the department.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const updateDepartment = (id, departmentData) => {
    return apiClient.put(`${API_URL}/${id}`, departmentData);
};

/**
 * Deletes a department by its ID.
 * @param {number|string} id - The ID of the department to delete.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const deleteDepartment = (id) => {
    return apiClient.delete(`${API_URL}/${id}`);
};

const departmentService = {
    getAllDepartments,
    getDepartmentById,
    createDepartment,
    updateDepartment,
    deleteDepartment,
};

export default departmentService;
