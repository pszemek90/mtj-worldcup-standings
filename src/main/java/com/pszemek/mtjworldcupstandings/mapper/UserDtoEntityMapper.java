package com.pszemek.mtjworldcupstandings.mapper;

import com.pszemek.mtjworldcupstandings.dto.UserDto;
import com.pszemek.mtjworldcupstandings.entity.User;

public class UserDtoEntityMapper {

    public static UserDto mapToDto(User entity) {
        return new UserDto()
                .setId(entity.getId())
                .setUsername(entity.getUsername())
                .setPassword(entity.getPassword())
                .setEmail(entity.getEmail())
                .setBalance(entity.getBalance());
    }
}
