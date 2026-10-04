package com.motria.notification;

import com.motria.config.AppProperties;
import com.motria.shared.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/** Envía los correos transaccionales (SMTP de Resend). */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final AppProperties properties;

    public void sendTemporaryPassword(String toEmail, String technicianName, String tempPassword, String workshopName) {
        send(toEmail,
                "Tu acceso a " + workshopName + " en Motria",
                EmailTemplates.temporaryPassword(technicianName, toEmail, tempPassword, workshopName));
    }

    public void sendPasswordReset(String toEmail, String userName, String resetUrl, long validMinutes) {
        send(toEmail,
                "Restablece tu contraseña en Motria",
                EmailTemplates.passwordReset(userName, resetUrl, validMinutes));
    }

    private void send(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(properties.mail().from(), properties.mail().fromName()));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
            log.info("Correo '{}' enviado a {}", subject, to);
        } catch (MessagingException | UnsupportedEncodingException | MailException e) {
            log.error("Fallo enviando correo a {}", to, e);
            throw new EmailDeliveryException("No se pudo enviar el correo. Inténtalo de nuevo en unos minutos.", e);
        }
    }
}
