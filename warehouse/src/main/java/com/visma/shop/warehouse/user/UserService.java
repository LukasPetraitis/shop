package com.visma.shop.warehouse.user;

import com.visma.shop.warehouse.user.entity.User;
import com.visma.shop.warehouse.user.entity.UserDTO;
import com.visma.shop.warehouse.user.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createUser(UserDTO userDTO){
        User user = new User();

        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUserRole(UserRole.USER.toString());

        userRepository.save(user);

    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
