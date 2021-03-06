package com.manish.photoapp.photoappusersservice.service;

import com.manish.photoapp.photoappusersservice.model.UserEntity;
import com.manish.photoapp.photoappusersservice.repository.UsersRepository;
import com.manish.photoapp.photoappusersservice.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {

    private final UsersRepository usersRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImp(UsersRepository usersRepository, BCryptPasswordEncoder encoder) {
        this.usersRepository = usersRepository;
        this.bCryptPasswordEncoder = encoder;
    }


    @Override
    public UserDto createUser(UserDto userDetails) {

        //giving a unique id to user
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        // transfer UserDto userDetails to UserEntity obj with the help of modelMapper
        ModelMapper modelMapper = new ModelMapper();
        // matching strictly the name of property for mapping.
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

        usersRepository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);

        return returnValue;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = usersRepository.findByEmail(username);
        if(userEntity == null) throw new UsernameNotFoundException(username);

        //since repo give userEntity type obj but we need to return UserDetails object.
        //there is a class User() that implements UserDetails so we will wrap userEntity inside User() object and return it.
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }


    @Override
    public UserDto getUserDetailsByEmail(String username) {
        UserEntity userEntity = usersRepository.findByEmail(username);
        if(userEntity == null) throw new UsernameNotFoundException(username);

        return new ModelMapper().map(userEntity, UserDto.class);
    }
}
