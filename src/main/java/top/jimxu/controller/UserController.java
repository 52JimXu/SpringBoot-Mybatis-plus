package top.jimxu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.jimxu.entity.User;
import top.jimxu.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User selById(@PathVariable int id){
        return userService.findUserById(id);
    }
    @GetMapping()
    public List<User> selUser(){
        return userService.selectList(null);
    }
    @PostMapping()
    public void addUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @PutMapping()
    public void updateUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @DeleteMapping()
    public void delUser(User user){
        userService.deleteUser(user);
    }
}
