//package sia.finance_tracker.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import sia.finance_tracker.entity.User;
//import sia.finance_tracker.service.UserService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserService userService;
//
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/register")
//    public ResponseEntity<User> registerUser(@RequestBody User user) {
//        User registeredUser = userService.registerUser(user);
//        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        User user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
//        User user = userService.updateUser(id, updatedUser);
//        return ResponseEntity.ok(user);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userService.deleteUser(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = userService.getAllUsers();
//        return ResponseEntity.ok(users);
//    }
//}

package sia.finance_tracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.finance_tracker.entity.User;
import sia.finance_tracker.service.UserService;
import sia.finance_tracker.security.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Users", description = "Users that can register,login,logout or delete their account.")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register a new user.",
            description = "Register a new user object. The response is user object with its id,email and username.",
            tags = { "users", "post" })
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get a user by Id.",
            description = "Get a user object by Id. The response is user object with its id,email and username.",
            tags = { "users", "get" })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Update user information by Id.",
            description = "Update a user object by Id. The response is user object with its updated id,email and username.",
            tags = { "users", "put" })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
        if (currentUser.getId().equals(id)) {
            updatedUser.setId(id);
            User user = userService.updateUser(id, updatedUser);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Delete a user by Id.",
            description = "Delete a user object by Id. The response is user object with its id,email and username being deleted.",
            tags = { "users", "delete" })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute("user");
        if (currentUser == null) {
            return new ResponseEntity<>("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }
        if (currentUser.getId().equals(id)) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.FORBIDDEN);
    }

    @Operation(
            summary = "Get all users.",
            description = "Get all user objects. The response is all user objects with its id,email and username.",
            tags = { "users", "get" })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Login feature
    @Operation(
            summary = "login a user.",
            description = "login a user object. The response is a user object with its id,email and username being able to access other endpoints.",
            tags = { "users", "post" })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        User user = userService.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            request.getSession().setAttribute("user", user);
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        }
        return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }

    // Logout feature
    @Operation(
            summary = "logout a user.",
            description = "logout a user object. The response is a user object with its id,email and username not being able to access other endpoints.",
            tags = { "users", "post" })
    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
        return new ResponseEntity<>("Logout successful", HttpStatus.OK);
    }
}






