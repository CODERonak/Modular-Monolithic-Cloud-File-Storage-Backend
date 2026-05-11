package com.product.CloudFileStorage.user.service.interfaces;

import com.product.CloudFileStorage.user.dto.*;

// Interface for User Service
public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest LoginRequest);
}
