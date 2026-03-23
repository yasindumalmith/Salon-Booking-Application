package com.yas.userservice.service.impl;

import com.yas.userservice.exception.UserException;
import com.yas.userservice.model.User;
import com.yas.userservice.repository.UserRepository;
import com.yas.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    @Override
    public void createUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) throws UserException {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()){
            return optionalUser.get();
        }else {
            throw new UserException("User not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUserById(Long id) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
        }else {
            throw new Exception("User not found with id" +id);
        }
    }

    @Override
    public User updateUser(Long id, User user) throws Exception {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setFullname(user.getFullname());
            existingUser.setEmail(user.getEmail());
            existingUser.setRole(user.getRole());
            return userRepository.save(existingUser);
        }else {
            throw new Exception("User not found");
        }
    }
}
