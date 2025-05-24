# Company Management Application

This is a full-stack application for managing company employees, departments, and projects. It features a Spring Boot backend with JWT authentication and a React frontend using Material-UI.

## Modules
- Employee Management
- Department Management
- Project Management
- Secure Authentication (JWT)

## Technology Stack

### Backend
- Java 17+
- Spring Boot (latest stable, e.g., 3.2.x)
- Spring Security (JWT Authentication)
- Spring Data JPA
- MySQL
- Swagger 3.0 (SpringDoc OpenAPI)
- JUnit 5 & Mockito

### Frontend
- React.js
- Material-UI
- Axios
- React Router

## Prerequisites

### Backend
- Java JDK 17 or later
- Maven 3.6+
- MySQL server running

### Frontend
- Node.js (v18.x or later recommended, check react-router-dom specific version requirements if issues arise)
- npm (usually comes with Node.js)

## Backend Setup

1.  **Clone the repository:**
    ```bash
    git clone <your-repo-url>
    cd <your-repo-name>
    ```
2.  **Database Configuration:**
    - Create a MySQL database (e.g., `company_management_db`).
    - Update the database connection properties in `src/main/resources/application.properties`:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/YOUR_DB_NAME # Replace YOUR_DB_NAME
      spring.datasource.username=YOUR_MYSQL_USERNAME # Replace YOUR_MYSQL_USERNAME
      spring.datasource.password=YOUR_MYSQL_PASSWORD # Replace YOUR_MYSQL_PASSWORD
      ```
3.  **Build the project:**
    From the root directory of the project (where the main `pom.xml` is):
    ```bash
    mvn clean install
    ```
4.  **Run the application:**
    ```bash
    mvn spring-boot:run
    ```
    Alternatively, you can run the packaged JAR file from the `target/` directory.
    The backend will typically start on `http://localhost:8080`.

## Frontend Setup (`company-management-frontend` directory)

1.  **Navigate to the frontend directory:**
    ```bash
    cd company-management-frontend
    ```
2.  **Install dependencies:**
    ```bash
    npm install
    ```
3.  **Run the application:**
    ```bash
    npm start
    ```
    The frontend development server will typically start on `http://localhost:3000`.

## Accessing API Documentation (Swagger UI)
Once the backend is running, you can access the Swagger UI for API documentation and testing at:
[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## Initial Login Credentials
- To log in, you will first need to create a user in the database or implement a data seeding mechanism.
- (TODO: Add instructions for creating an initial admin user, e.g., through a data.sql script or a dedicated registration API if implemented).
  For now, you might need to manually insert a user into the `user` table. The password should be BCrypt encoded. Example User:
    - username: `admin`
    - password: (BCrypt encoded version of a password, e.g., `adminpass`)
    - roles: `ROLE_ADMIN,ROLE_USER`

---
*This README is a starting point and should be updated as the project evolves.*
