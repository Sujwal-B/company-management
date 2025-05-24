import React from 'react';
import { Link as RouterLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Box, Container, Typography, Grid, Card, CardContent, CardActionArea, Avatar } from '@mui/material';
import PeopleIcon from '@mui/icons-material/People';
import BusinessIcon from '@mui/icons-material/Business';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import GroupWorkIcon from '@mui/icons-material/GroupWork'; // Example icon for projects

const DashboardPage = () => {
    const { user } = useAuth(); // Get user from context, if available

    // Placeholder data for summary metrics
    const summaryMetrics = [
        { title: 'Total Employees', value: '150', icon: <PeopleIcon fontSize="large" color="primary" />, link: '/employees' },
        { title: 'Active Projects', value: '25', icon: <GroupWorkIcon fontSize="large" color="secondary" />, link: '/projects' },
        { title: 'Departments', value: '10', icon: <BusinessIcon fontSize="large" color="success" />, link: '/departments' },
    ];

    // Placeholder data for navigation links
    const navLinks = [
        { title: 'Manage Employees', path: '/employees', description: 'View, add, and edit employee data.' },
        { title: 'Manage Departments', path: '/departments', description: 'Organize and manage company departments.' },
        { title: 'Manage Projects', path: '/projects', description: 'Track and manage company projects.' },
    ];

    return (
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
            <Typography variant="h4" gutterBottom component="h1">
                {user && user.username ? `Welcome, ${user.username}!` : 'Welcome to the Dashboard!'}
            </Typography>

            {/* Summary Metrics Section */}
            <Typography variant="h5" component="h2" gutterBottom sx={{ mt: 4 }}>
                Key Metrics
            </Typography>
            <Grid container spacing={3}>
                {summaryMetrics.map((metric) => (
                    <Grid item xs={12} sm={6} md={4} key={metric.title}>
                        <Card elevation={3}>
                           <CardActionArea component={RouterLink} to={metric.link}>
                                <CardContent sx={{ display: 'flex', alignItems: 'center', flexDirection: 'column', p: 3 }}>
                                    <Avatar sx={{ bgcolor: 'background.paper', mb: 1, width: 56, height: 56 }}>
                                        {metric.icon}
                                    </Avatar>
                                    <Typography variant="h6" component="div">
                                        {metric.title}
                                    </Typography>
                                    <Typography variant="h4" color="text.secondary">
                                        {metric.value}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                        </Card>
                    </Grid>
                ))}
            </Grid>

            {/* Navigation Links Section */}
            <Typography variant="h5" component="h2" gutterBottom sx={{ mt: 5 }}>
                Management Sections
            </Typography>
            <Grid container spacing={3}>
                {navLinks.map((link) => (
                    <Grid item xs={12} sm={6} md={4} key={link.title}>
                        <Card elevation={3} sx={{ height: '100%' }}>
                            <CardActionArea component={RouterLink} to={link.path} sx={{ height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'flex-start' }}>
                                <CardContent sx={{textAlign: 'center'}}>
                                    <Typography gutterBottom variant="h6" component="div">
                                        {link.title}
                                    </Typography>
                                    <Typography variant="body2" color="text.secondary">
                                        {link.description}
                                    </Typography>
                                </CardContent>
                            </CardActionArea>
                        </Card>
                    </Grid>
                ))}
            </Grid>
        </Container>
    );
};

export default DashboardPage;
