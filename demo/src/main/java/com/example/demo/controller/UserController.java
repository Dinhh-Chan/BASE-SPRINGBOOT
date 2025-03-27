package com.example.demo.controller;

import com.example.demo.common.controller.BaseController;
import com.example.demo.common.dto.ApiResponse;
import com.example.demo.dto.request.CreateUserRequest;
import com.example.demo.dto.request.UpdateUserRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations pertaining to users in the application")
public class UserController extends BaseController<User, Long, UserService> {

    public UserController(UserService userService) {
        super(userService);
    }

    // Ghi đè phương thức từ BaseController để tránh xung đột mapping
    @Override
    @GetMapping("/base")
    public ResponseEntity<List<User>> getAll() {
        return super.getAll();
    }

    @Operation(summary = "Get all users", description = "Returns a list of all active users")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users",
                content = @Content(mediaType = "application/json", 
                array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = service.findAllActive().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/base/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return super.getById(id);
    }

    @Operation(summary = "Get user by ID", description = "Returns a specific user by their ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the user",
                content = @Content(mediaType = "application/json", 
                schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve", required = true) 
            @PathVariable Long id) {
        User user = service.getByIdActive(id);
        return ResponseEntity.ok(convertToResponse(user));
    }

    @Operation(summary = "Get user by username", description = "Returns a specific user by their username")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve", required = true) 
            @PathVariable String username) {
        User user = service.findByUsername(username);
        return ResponseEntity.ok(convertToResponse(user));
    }

    @Operation(summary = "Get user by email", description = "Returns a specific user by their email address")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(
            @Parameter(description = "Email of the user to retrieve", required = true) 
            @PathVariable String email) {
        User user = service.findByEmail(email);
        return ResponseEntity.ok(convertToResponse(user));
    }
    
    // Ghi đè phương thức từ BaseController để tránh xung đột mapping
    @Override
    @PostMapping("/base")
    public ResponseEntity<User> create(@RequestBody User entity) {
        return super.create(entity);
    }

    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User successfully created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input or username/email already exists")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User details for registration", 
                required = true,
                content = @Content(schema = @Schema(implementation = CreateUserRequest.class)))
            @Valid @RequestBody CreateUserRequest createUserRequest) {
        
        // Check if username is available
        if (!service.isUsernameAvailable(createUserRequest.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Check if email is available
        if (!service.isEmailAvailable(createUserRequest.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = convertToEntity(createUserRequest);
        User savedUser = service.save(user);
        return new ResponseEntity<>(convertToResponse(savedUser), HttpStatus.CREATED);
    }

    // Ghi đè phương thức từ BaseController để tránh xung đột mapping
    @Override
    @PutMapping("/base/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User entity) {
        return super.update(id, entity);
    }

    @Operation(summary = "Update an existing user", description = "Updates an existing user's information")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User successfully updated"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID of the user to update", required = true) 
            @PathVariable Long id, 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Updated user information", 
                required = true,
                content = @Content(schema = @Schema(implementation = UpdateUserRequest.class)))
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        
        User existingUser = service.getById(id);
        User userToUpdate = updateEntity(existingUser, updateUserRequest);
        User updatedUser = service.update(id, userToUpdate);
        
        return ResponseEntity.ok(convertToResponse(updatedUser));
    }

    @Operation(summary = "Delete a user", description = "Soft deletes a user by marking them as inactive")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User successfully deleted"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @Parameter(description = "ID of the user to delete", required = true) 
            @PathVariable Long id) {
        return super.delete(id);
    }

    // Helper methods
    private User convertToEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setBirthDate(request.getBirthDate());
        return user;
    }
    
    private User updateEntity(User user, UpdateUserRequest request) {
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(request.getPassword());
        }
        return user;
    }
    
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setBirthDate(user.getBirthDate());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}