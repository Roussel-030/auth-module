package com.myCompany.projectAuthentication.authentication.service.emailService;

import com.myCompany.projectAuthentication.authentication.enumeration.EmailTemplateName;
import jakarta.mail.MessagingException;

public interface EmailServiceInterface {
    void sendEmail(String to, EmailTemplateName emailTemplate, String confirmationUrl, String activationCode, String subject) throws MessagingException;
}
