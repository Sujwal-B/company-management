import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#1976D2', // Deep Blue
    },
    secondary: {
      main: '#009688', // Vibrant Teal
    },
    background: {
      default: '#F5F5F5', // Light Gray
    },
    text: {
      primary: '#333333', // Dark Gray
    },
  },
  typography: {
    fontFamily: ['Roboto', 'Helvetica Neue', 'Arial', 'sans-serif'].join(','),
  },
});

export default theme;
