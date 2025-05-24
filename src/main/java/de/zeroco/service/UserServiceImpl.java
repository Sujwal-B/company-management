package de.zeroco.service;

import de.zeroco.dto.RegistrationRequest;
import de.zeroco.dto.UserResponse;
import de.zeroco.exception.EmailAlreadyExistsException;
import de.zeroco.exception.UserAlreadyExistsException;
import de.zeroco.model.User;
import de.zeroco.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegistrationRequest registrationRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username already exists: " + registrationRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + registrationRequest.getEmail());
        }

        // Create new user
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setEmail(registrationRequest.getEmail());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setRoles("ROLE_USER"); // Default role

        // Save user
        User savedUser = userRepository.save(user);

        // Send confirmation email
        emailService.sendRegistrationConfirmationEmail(savedUser);

        // Return UserResponse DTO
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getRoles()
        );
    }
}
