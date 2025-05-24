package de.zeroco.service;

import de.zeroco.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final Environment env;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender, Environment env) {
        this.javaMailSender = javaMailSender;
        this.env = env;
    }

    @Override
    public void sendRegistrationConfirmationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        String fromEmail = env.getProperty("spring.mail.from", "noreply@localhost");
        message.setFrom(fromEmail);
        message.setTo(user.getEmail());
        message.setSubject("Registration Successful");
        message.setText("Dear " + user.getFirstName() + ",\n\nThank you for registering!");

        try {
            javaMailSender.send(message);
            logger.info("Registration confirmation email sent successfully to {}", user.getEmail());
        } catch (MailException e) {
            logger.error("Failed to send registration confirmation email to {}: {}", user.getEmail(), e.getMessage());
            // Optionally, rethrow a custom exception or handle accordingly
        }
    }
}
