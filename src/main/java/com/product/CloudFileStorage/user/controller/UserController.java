package com.product.CloudFileStorage.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.product.CloudFileStorage.user.dto.LoginRequest;
import com.product.CloudFileStorage.user.dto.LoginResponse;
import com.product.CloudFileStorage.user.dto.RegisterRequest;
import com.product.CloudFileStorage.user.dto.RegisterResponse;
import com.product.CloudFileStorage.user.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Registers a new user through email, fullname and password")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        var response = userService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Login's the user through email and password and generates JWT token")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid LoginRequest loginRequest) {
        var response = userService.loginUser(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
