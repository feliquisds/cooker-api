package com.expsn.cooker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailTester implements CommandLineRunner {

    private final JavaMailSender mailSender;

    @Value("${app.email-tester.enabled:false}")
    private boolean emailTesterEnabled;

    @Override
    public void run(String... args) throws Exception {
        if (!emailTesterEnabled) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("cooker@expsn.onmicrosoft.com");
            helper.setTo("feliquisds@outlook.com");
            helper.setSubject("Teste de configuração de email - Cooker");
            helper.setText("Este é um email de teste para verificar a configuração do JavaMailSender no Cooker.", "<p>Este é um email de teste para verificar a configuração do <strong>JavaMailSender</strong> no Cooker.</p>");
            mailSender.send(message);
            System.out.println(">>> Email configuration is valid. JavaMailSender is working.");
        } catch (Exception e) {
            System.err.println(">>> Email configuration is invalid. Failed to create MimeMessage.");
            e.printStackTrace();
        }


    }
}
