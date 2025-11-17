package com.globus.userservice.controller;

import com.globus.userservice.dto.AuthRequest;
import com.globus.userservice.dto.AuthResponse;
import com.globus.userservice.entity.User;
import com.globus.userservice.exception.AppError;
import com.globus.userservice.service.interfaces.UserService;
import com.globus.userservice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(
            @RequestBody AuthRequest authRequest,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(),
                    "Некорректный email или пароль."), HttpStatus.UNAUTHORIZED);
        }
        User user = userService.getUserByEmail(authRequest.getEmail());
        String token = jwtTokenUtil.generateToken(user);
        AuthResponse authResponse = new AuthResponse(token, user.getId().toString());
        return ResponseEntity.ok(authResponse);
    }
}
