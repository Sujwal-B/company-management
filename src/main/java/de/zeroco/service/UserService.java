package de.zeroco.service;

import de.zeroco.dto.RegistrationRequest;
import de.zeroco.dto.UserResponse;

public interface UserService {
    UserResponse registerUser(RegistrationRequest registrationRequest);
}
