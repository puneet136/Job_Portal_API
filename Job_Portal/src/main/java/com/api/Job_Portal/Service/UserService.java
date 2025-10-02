package com.api.Job_Portal.Service;


import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.dto.UserDTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    //For Registering the User
    User registerUser(UserDTO userDto);

    //For Find User using the email
    Optional<User> findByEmail(String email);

    //For Find user using ID
    User getUserById(Long userId);

    //For listed all the users in one query
    List<User> listAllUsers();

    //For update User using id
    User updateUser(Long id, User updatedUser);

    //For deleting user using particular id
    void deleteUser(Long id);

    //For fetch the details of current user using its email
    User getCurrentUser(String email);
}

