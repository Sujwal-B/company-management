import apiClient from './api'; // Assuming apiClient is the configured Axios instance

const API_URL = '/projects'; // Base URL for project endpoints

export const getAllProjects = () => {
    return apiClient.get(API_URL);
};

export const getProjectById = (id) => {
    return apiClient.get(`${API_URL}/${id}`);
};

/**
 * Creates a new project.
 * @param {object} projectData - The data for the new project.
 *                               This might include an array of employee IDs, e.g., { name: "...", employeeIds: [1, 2] }
 *                               or a Set/Array of Employee objects if the backend handles it.
 *                               For now, let's assume it can take { ..., employees: [{id:1}, {id:2}] } or similar.
 *                               The backend DTO for Project needs to be able to accept this.
 */
export const createProject = (projectData) => {
    return apiClient.post(API_URL, projectData);
};

/**
 * Updates an existing project.
 * @param {number|string} id - The ID of the project to update.
 * @param {object} projectData - The updated data for the project. Similar to createProject,
 *                               this might include updated employee assignments.
 */
export const updateProject = (id, projectData) => {
    return apiClient.put(`${API_URL}/${id}`, projectData);
};

export const deleteProject = (id) => {
    return apiClient.delete(`${API_URL}/${id}`);
};

export const assignEmployeeToProject = (projectId, employeeId) => {
    return apiClient.post(`${API_URL}/${projectId}/employees/${employeeId}`);
};

export const removeEmployeeFromProject = (projectId, employeeId) => {
    return apiClient.delete(`${API_URL}/${projectId}/employees/${employeeId}`);
};

const projectService = {
    getAllProjects,
    getProjectById,
    createProject,
    updateProject,
    deleteProject,
    assignEmployeeToProject,
    removeEmployeeFromProject,
};

export default projectService;
