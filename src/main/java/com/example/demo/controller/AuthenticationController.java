package com.example.demo.controller;

import com.example.demo.infraestrutura.TokenService;
import com.example.demo.model.user.AuthenticationDTO;
import com.example.demo.model.user.LoginResponseDTO;
import com.example.demo.model.user.RegisterDTO;
import com.example.demo.model.user.User;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var authToken = createAuthenticationToken(data);
        var auth = authenticationManager.authenticate(authToken);

        String token = tokenService.generateToken((User) auth.getPrincipal());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        if (isUserAlreadyRegistered(data.login())) {
            return ResponseEntity.badRequest().build();
        }

        saveNewUser(data);
        return ResponseEntity.ok().build();
    }

    private UsernamePasswordAuthenticationToken createAuthenticationToken(AuthenticationDTO data) {
        return new UsernamePasswordAuthenticationToken(data.login(), data.password());
    }

    private boolean isUserAlreadyRegistered(String login) {
        return repository.findByLogin(login) != null;
    }

    private void saveNewUser(RegisterDTO data) {
        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());
        repository.save(newUser);
    }
}
