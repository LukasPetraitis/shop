package com.visma.warehouseApp.user;

import com.visma.warehouseApp.user.entity.User;
import com.visma.warehouseApp.user.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public HttpStatus createUser(@RequestBody UserDTO userDTO){
        userService.createUser(userDTO);
        return HttpStatus.OK;
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

}
