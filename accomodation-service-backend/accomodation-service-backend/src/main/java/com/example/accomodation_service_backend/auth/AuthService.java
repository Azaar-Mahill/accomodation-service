package com.example.accomodation_service_backend.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;    // 👈 inject
    private final UserTokenRepository tokenRepository;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserTokenRepository tokenRepository) {         // 👈 ctor
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
    }

    public void signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // hash
        user.setRole(request.getRole());

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        // save token to DB
        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setToken(token);
        userToken.setCreatedAt(LocalDateTime.now());

        // 12 hours from now (same as JWT expiry)
        userToken.setExpiresAt(LocalDateTime.now().plusHours(12));

        tokenRepository.save(userToken);

        return new LoginResponse(user.getEmail(), user.getRole(), token);
    }

    public void logout(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }

}
