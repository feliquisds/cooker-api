package com.expsn.cooker.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.internet.MimeMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        notificationClient = new NotificationClient(mailSender, "no-reply@cooker.test");
    }

    @Test
    void sendRecoveryEmailBuildsAndSendsMessage() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notificationClient.sendRecoveryEmail("user@example.com");

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        verify(mailSender).createMimeMessage();
        assertThat(messageCaptor.getValue()).isSameAs(mimeMessage);
    }

    @Test
    void sendRecoveryEmailIgnoresBlankEmail() {
        notificationClient.sendRecoveryEmail(" ");

        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}