package de.zeroco.service;

import de.zeroco.model.User;

public interface EmailService {
    void sendRegistrationConfirmationEmail(User user);
}
