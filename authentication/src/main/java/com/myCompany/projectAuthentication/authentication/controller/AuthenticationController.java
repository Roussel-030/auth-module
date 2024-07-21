package com.myCompany.projectAuthentication.authentication.controller;

import com.myCompany.projectAuthentication.authentication.dto.AuthenticationRequest;
import com.myCompany.projectAuthentication.authentication.dto.AuthenticationResponse;
import com.myCompany.projectAuthentication.authentication.dto.RegistrationRequest;
import com.myCompany.projectAuthentication.authentication.service.authenticationService.AuthenticationServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationServiceInterface service;

    @PostMapping(path = "register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) throws MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping(path = "activate-account")
    public void confirm(@RequestParam String token) throws MessagingException {
        service.activateAccount(token);
    }

}
