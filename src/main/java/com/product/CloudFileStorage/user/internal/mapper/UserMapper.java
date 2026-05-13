package com.product.CloudFileStorage.user.internal.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.product.CloudFileStorage.user.internal.dto.*;
import com.product.CloudFileStorage.user.internal.model.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(RegisterRequest registerRequest);

    @Mapping(target = "role", ignore = true)
    RegisterResponse toRegisterResponse(User user);

    @Mapping(target = "token", ignore = true)
    LoginResponse toLoginResponse(User user);
}
