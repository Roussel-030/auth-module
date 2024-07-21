package com.myCompany.projectAuthentication.authentication.service.authenticationService;

import com.myCompany.projectAuthentication.authentication.dto.AuthenticationRequest;
import com.myCompany.projectAuthentication.authentication.dto.AuthenticationResponse;
import com.myCompany.projectAuthentication.authentication.dto.RegistrationRequest;
import jakarta.mail.MessagingException;

public interface AuthenticationServiceInterface {
    void register(RegistrationRequest request) throws MessagingException;
    AuthenticationResponse authenticate(AuthenticationRequest request);
    void activateAccount(String token) throws MessagingException;
}
