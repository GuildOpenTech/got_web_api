package org.got.web.gotweb.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.user.domain.GotUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    @Value("${app.url}")
    private String appUrl;

    public void sendVerificationEmail(GotUser user, String token) {
        String subject = "Vérification de votre compte " + appName;
        String template = "email-verification";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("verificationUrl", appUrl + "/verify-email?token=" + token);
        variables.put("appName", appName);

        sendTemplatedEmail(user.getEmail(), subject, template, variables);
    }

    public void sendPasswordResetEmail(GotUser user, String token) {
        String subject = "Réinitialisation de votre mot de passe " + appName;
        String template = "password-reset";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("resetUrl", appUrl + "/reset-password");
        variables.put("appName", appName);

        sendTemplatedEmail(user.getEmail(), subject, template, variables);
    }

    public void sendPasswordChangeNotification(GotUser user) {
        String subject = "Modification de votre mot de passe " + appName;
        String template = "password-changed";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("appName", appName);

        sendTemplatedEmail(user.getEmail(), subject, template, variables);
    }

    private void sendTemplatedEmail(String to, String subject, String template, Map<String, Object> variables) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process(template, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);

            emailSender.send(message);
            log.info("Email envoyé avec succès à : {}", to);
        } catch (MessagingException e) {
            log.error("Erreur lors de l'envoi de l'email à {} : {}", to, e.getMessage());
            throw new RuntimeException("Erreur lors de l'envoi de l'email", e);
        }
    }
}
