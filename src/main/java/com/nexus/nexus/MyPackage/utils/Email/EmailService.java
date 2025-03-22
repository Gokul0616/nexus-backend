package com.nexus.nexus.MyPackage.utils.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Sends an email with HTML content.
     *
     * @param to          the recipient's email address
     * @param subject     the subject of the email
     * @param htmlContent the HTML content of the email
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // Set the "from" address - make sure it matches your configured email
            helper.setFrom("your.email@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            // Set the content and mark it as HTML
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            // Log or rethrow the exception as needed
            e.printStackTrace();
        }
    }

    /**
     * Optionally, you can keep your plain text method as well.
     */
    public void sendEmail(String to, String subject, String content) {
        // Fallback to plain text or call sendHtmlEmail with non-HTML content
        sendHtmlEmail(to, subject, "<pre>" + content + "</pre>");
    }
}
