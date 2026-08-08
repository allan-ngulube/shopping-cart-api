package com.allan.notification_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Service
public class NotificationService {

    private final SesV2Client sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    public NotificationService(SesV2Client sesClient) {
        this.sesClient = sesClient;
    }

    public void sendNotification(String toEmail, String message) {

        Destination destination = Destination.builder()
                .toAddresses(toEmail)
                .build();

        Message emailMessage = Message.builder()
                .subject(
                        Content.builder()
                                .data("Order Confirmation")
                                .build()
                )
                .body(
                        Body.builder()
                                .text(
                                        Content.builder()
                                                .data(message)
                                                .build()
                                )
                                .build()
                )
                .build();

        SendEmailRequest request = SendEmailRequest.builder()
                .fromEmailAddress(fromEmail)
                .destination(destination)
                .content(
                        EmailContent.builder()
                                .simple(emailMessage)
                                .build()
                )
                .build();

        sesClient.sendEmail(request);
    }
}