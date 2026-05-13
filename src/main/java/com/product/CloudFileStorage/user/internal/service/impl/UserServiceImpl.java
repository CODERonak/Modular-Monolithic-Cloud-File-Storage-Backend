package com.product.CloudFileStorage.user.internal.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.product.CloudFileStorage.common.exceptions.custom.UserNotFoundException;
import com.product.CloudFileStorage.user.internal.dto.LoginRequest;
import com.product.CloudFileStorage.user.internal.dto.LoginResponse;
import com.product.CloudFileStorage.user.internal.dto.RegisterRequest;
import com.product.CloudFileStorage.user.internal.dto.RegisterResponse;
import com.product.CloudFileStorage.user.internal.exception.InvalidCredentialsException;
import com.product.CloudFileStorage.user.internal.exception.UserAlreadyExistsException;
import com.product.CloudFileStorage.user.internal.mapper.UserMapper;
import com.product.CloudFileStorage.user.internal.model.enums.Role;
import com.product.CloudFileStorage.user.internal.repository.UserRepository;
import com.product.CloudFileStorage.user.internal.security.jwt.JWTUtil;
import com.product.CloudFileStorage.user.internal.service.interfaces.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail()))
            throw new UserAlreadyExistsException("user already exists with this email");

        var user = userMapper.toEntity(registerRequest);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        return userMapper.toRegisterResponse(user);
    }

    @Override
    public LoginResponse loginUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtil.generateToken(loginRequest.getEmail());

            var user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("user not found"));

            return new LoginResponse(user.getEmail(), user.getFullname(), jwtToken);

        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }

}
