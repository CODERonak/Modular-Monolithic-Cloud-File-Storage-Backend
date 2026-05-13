package com.product.CloudFileStorage.user.internal.service.interfaces;

import com.product.CloudFileStorage.user.internal.dto.*;

// Interface for User Service
public interface UserService {
    RegisterResponse registerUser(RegisterRequest registerRequest);

    LoginResponse loginUser(LoginRequest LoginRequest);
}
