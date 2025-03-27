package com.example.demo.controller;

import com.example.demo.common.dto.ApiResponse;
import com.example.demo.common.security.config.JwtConfig;
import com.example.demo.common.security.dto.JwtResponse;
import com.example.demo.common.security.dto.LoginRequest;
import com.example.demo.common.security.util.JwtTokenUtil;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, 
                         JwtConfig jwtConfig, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Operation(summary = "Authenticate user", description = "Authenticates user credentials and returns JWT token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully authenticated", 
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = JwtResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Xác thực từ username và password
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        
        // Cập nhật SecurityContext sử dụng Authentication
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Tạo token
        String jwt = jwtTokenUtil.generateToken(authentication);
        
        // Thời điểm hết hạn
        Date expirationDate = jwtTokenUtil.extractExpiration(jwt);
        
        return ResponseEntity.ok(new JwtResponse(jwt, jwtConfig.getHeader(), loginRequest.getUsername(), expirationDate));
    }

    @Operation(summary = "Validate token", description = "Checks if a token is valid")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token is valid"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token is invalid")
    })
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse> validateToken(
            @RequestBody String token) {
        boolean isValid = jwtTokenUtil.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse(true, "Token is valid"));
        } else {
            return ResponseEntity.status(401).body(new ApiResponse(false, "Token is invalid or expired"));
        }
    }
}