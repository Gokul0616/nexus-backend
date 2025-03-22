package com.nexus.nexus.MyPackage.utils.Email;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Google's SMTP settings
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Replace with your actual Gmail address and Google App Password
        mailSender.setUsername("gokul.363you@gmail.com");
        mailSender.setPassword("urhf thdz kkki nmxd");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
