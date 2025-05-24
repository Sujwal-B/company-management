import apiClient from './api'; // Assuming apiClient is the configured Axios instance

const API_URL = '/departments'; // Base URL for department endpoints relative to apiClient's baseURL

/**
 * Fetches all departments.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const getAllDepartments = () => {
    return apiClient.get(API_URL);
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
