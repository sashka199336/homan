package com.globus.userservice.integration;

import org.springframework.stereotype.Component;

@Component
public class IntegrationMessageSender {

    public void sendMessage() {
        String message = "Message";
        System.out.println("Sent message: " + message);
    }
}
