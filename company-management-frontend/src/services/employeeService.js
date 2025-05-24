import apiClient from './api'; // Assuming apiClient is the configured Axios instance

const API_URL = '/employees'; // Base URL for employee endpoints relative to apiClient's baseURL

/**
 * Fetches all employees.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const getAllEmployees = () => {
    return apiClient.get(API_URL);
};

/**
 * Fetches a single employee by their ID.
 * @param {number|string} id - The ID of the employee.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const getEmployeeById = (id) => {
    return apiClient.get(`${API_URL}/${id}`);
};

/**
 * Creates a new employee.
 * @param {object} employeeData - The data for the new employee.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const createEmployee = (employeeData) => {
    return apiClient.post(API_URL, employeeData);
};

/**
 * Updates an existing employee.
 * @param {number|string} id - The ID of the employee to update.
 * @param {object} employeeData - The updated data for the employee.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const updateEmployee = (id, employeeData) => {
    return apiClient.put(`${API_URL}/${id}`, employeeData);
};

/**
 * Deletes an employee by their ID.
 * @param {number|string} id - The ID of the employee to delete.
 * @returns {Promise<AxiosResponse<any>>} The promise from the apiClient call.
 */
export const deleteEmployee = (id) => {
    return apiClient.delete(`${API_URL}/${id}`);
};

const employeeService = {
    getAllEmployees,
    getEmployeeById,
    createEmployee,
    updateEmployee,
    deleteEmployee,
};

export default employeeService;
