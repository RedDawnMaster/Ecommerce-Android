package org.sop.ecommerceapp.controllers;

import java.util.List;

import org.sop.ecommerceapp.models.User;
import org.sop.ecommerceapp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{username}/{password}")
    public User login(@PathVariable String username, @PathVariable String password) {
        return userService.login(username, password);
    }

    @GetMapping("/username/{username}")
    public List<User> findByUsernameContains(@PathVariable String username) {
        return this.userService.findByUsernameContains(username);
    }

    @GetMapping("/email/{email}")
    public List<User> findByEmailContains(@PathVariable String email) {
        return this.userService.findByEmailContains(email);
    }

    @GetMapping("/{role}")
    public List<User> findByRole(@PathVariable String role) {
        return this.userService.findByRole(role);
    }

    @GetMapping("/")
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/addToWishList/{username}/{label}")
    public int addToWishList(@PathVariable String username, @PathVariable String label) {
        return userService.addToWishList(username, label);
    }

    @GetMapping("/removeFromWishList/{username}/{label}")
    public void removeFromWishList(@PathVariable String username, @PathVariable String label) {
        userService.removeFromWishList(username, label);
    }

    @DeleteMapping("/{username}")
    public void deleteByUsername(@PathVariable String username) {
         userService.deleteByUsername(username);
    }

    @PostMapping("/")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PutMapping("/")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

}
