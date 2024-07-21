package com.myCompany.projectAuthentication.authentication.service.authenticationService;

import com.myCompany.projectAuthentication.authentication.dto.AuthenticationRequest;
import com.myCompany.projectAuthentication.authentication.dto.AuthenticationResponse;
import com.myCompany.projectAuthentication.authentication.dto.RegistrationRequest;
import com.myCompany.projectAuthentication.authentication.entity.Token;
import com.myCompany.projectAuthentication.authentication.entity.User;
import com.myCompany.projectAuthentication.authentication.enumeration.EmailTemplateName;
import com.myCompany.projectAuthentication.authentication.repository.RoleRepository;
import com.myCompany.projectAuthentication.authentication.repository.TokenRepository;
import com.myCompany.projectAuthentication.authentication.repository.UserRepository;
import com.myCompany.projectAuthentication.authentication.service.emailService.EmailServiceInterface;
import com.myCompany.projectAuthentication.authentication.service.jwtService.JwtServiceInterface;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailServiceInterface emailService;
    private final JwtServiceInterface jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${mailing.frontend.activation_url}")
    private String activationUrl;

    @Override
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));

        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User)auth.getPrincipal());
        claims.put("User id", user.getUserId());
        var jwtToken = jwtService.generateToken(claims, user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }
        var user = userRepository.findById(savedToken.getUser().getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex =  secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}
