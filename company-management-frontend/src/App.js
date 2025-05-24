import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import EmployeePage from './pages/EmployeePage';
import DepartmentPage from './pages/DepartmentPage';
import ProjectPage from './pages/ProjectPage';
import ProfilePage from './pages/ProfilePage';
import ProtectedRoute from './components/ProtectedRoute';
import { useAuth } from './context/AuthContext';
import Notifier from './components/common/Notifier'; // Import Notifier
import { AppBar, Toolbar, Typography, Button, Container, Box } from '@mui/material';


// Placeholder for a public Home page (optional)
const HomePage = () => (
    <Container>
        <Typography variant="h4" sx={{ mt: 4 }}>Home Page</Typography>
        <Typography sx={{ mt: 2 }}>This is a public home page.</Typography>
        <Button component={Link} to="/login" variant="contained" color="primary" sx={{ mr: 2, mt: 2}}>
            Login
        </Button>
         <Button component={Link} to="/dashboard" variant="outlined" color="primary" sx={{ mt: 2}}>
            Go to Dashboard (Protected)
        </Button>
    </Container>
);


function App() {
    const { isAuthenticated, logout } = useAuth();

    return (
        <Router>
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        Company Management
                    </Typography>
                    <Button color="inherit" component={Link} to="/">Home</Button>
                    {isAuthenticated ? (
                        <>
                            <Button color="inherit" component={Link} to="/profile">Profile</Button>
                            <Button color="inherit" onClick={logout}>Logout</Button>
                        </>
                    ) : (
                        <Button color="inherit" component={Link} to="/login">Login</Button>
                    )}
                </Toolbar>
            </AppBar>
            <Notifier /> {/* Add Notifier here to be available globally */}
            <Box component="main" sx={{ p: 3 }}>
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/" element={<HomePage />} /> {/* Optional Home Page */}
                    
                    {/* Protected Routes */}
                    <Route element={<ProtectedRoute />}>
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/employees" element={<EmployeePage />} />
                        <Route path="/departments" element={<DepartmentPage />} />
                        <Route path="/projects" element={<ProjectPage />} />
                        <Route path="/profile" element={<ProfilePage />} /> {/* Add ProfilePage route */}
                    </Route>
                    
                    {/* Fallback for unmatched routes (optional) */}
                    <Route path="*" element={
                        <Container sx={{mt: 4}}>
                            <Typography variant="h5">404 - Page Not Found</Typography>
                            <Link to="/">Go to Home</Link>
                        </Container>
                    } />
                </Routes>
            </Box>
        </Router>
    );
}

export default App;
