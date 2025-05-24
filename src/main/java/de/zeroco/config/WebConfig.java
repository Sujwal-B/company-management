package de.zeroco.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Apply to all paths in the application
                .allowedOrigins(
                        "http://localhost:3000", // Common React dev port
                        "http://localhost:4200", // Common Angular dev port
                        "http://localhost:8080", // Common Vue.js dev port or other backend services
                        "http://localhost:5173"  // Common Vite dev port
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow all standard and custom headers
                .allowCredentials(true) // Allow cookies and authorization headers
                .maxAge(3600); // Cache pre-flight response for 1 hour
    }
}
