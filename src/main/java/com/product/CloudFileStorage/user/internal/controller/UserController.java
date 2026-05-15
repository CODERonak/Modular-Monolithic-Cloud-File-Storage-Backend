package com.product.CloudFileStorage.user.internal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product.CloudFileStorage.user.internal.dto.LoginRequest;
import com.product.CloudFileStorage.user.internal.dto.LoginResponse;
import com.product.CloudFileStorage.user.internal.dto.RegisterRequest;
import com.product.CloudFileStorage.user.internal.dto.RegisterResponse;
import com.product.CloudFileStorage.user.internal.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "Authentication", description = "Endpoints for user registration and login. " +
        "Returns a JWT token on successful login.")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Registers a new user account with email, password and full name)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "409", description = "User already exists with this email"),
            @ApiResponse(responseCode = "400", description = "Invalid request — validation failed")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        var response = userService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Login with existing credentials", description = "Authenticates a user with email and password. "
            +
            "Returns a JWT token valid for 24 hours. " +
            "Use this token in the Authorization header as: Bearer {token}.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "400", description = "Invalid request — validation failed")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        var response = userService.loginUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
