package com.manish.photoapp.photoappusersservice.controllers;

import com.manish.photoapp.photoappusersservice.model.CreateUserModel;
import com.manish.photoapp.photoappusersservice.model.UserEntity;
import com.manish.photoapp.photoappusersservice.model.UserResponseModel;
import com.manish.photoapp.photoappusersservice.service.UserService;
import com.manish.photoapp.photoappusersservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @GetMapping("/status/check")
    public String status() {
        return "working on port: " + env.getProperty("local.server.port");
    }

    //for xml support jackson xml dependency is required
    @PostMapping(
            consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            ,produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
            )
    public ResponseEntity<UserResponseModel> createUser(@Valid @RequestBody CreateUserModel userDetails) {

        ModelMapper modelMapper = new ModelMapper();
        // matching strictly the name of property for mapping.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);

        UserResponseModel returnValue = modelMapper.map(createdUser, UserResponseModel.class);

//        return new ResponseEntity.(HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }
}
