package com.manish.photoapp.photoappusersservice.service;

import com.manish.photoapp.photoappusersservice.shared.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDetails);
}
