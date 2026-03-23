package com.yas.userservice.service;

import com.yas.userservice.exception.UserException;
import com.yas.userservice.model.User;

import java.util.List;

public interface UserService {
    void createUser(User user);
    User getUserById(Long id) throws UserException;
    List<User> getAllUsers();
    void deleteUserById(Long id) throws Exception;
    User updateUser(Long id, User user) throws Exception;




}
