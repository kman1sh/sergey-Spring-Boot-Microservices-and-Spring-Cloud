package com.manish.photoapp.photoappusersservice.repository;

import com.manish.photoapp.photoappusersservice.model.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Long> {
}
