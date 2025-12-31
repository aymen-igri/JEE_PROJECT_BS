package com.backend.backend.controller;

import com.backend.backend.dto.request.User.VerifDuplRequest;
import com.backend.backend.service.User.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/checkExistence")
    public ResponseEntity<?> checkUserExistence(
            @Valid @RequestBody VerifDuplRequest request
    ) throws Exception {
        userService.userExists(request);
        return ResponseEntity.ok().build();
    }
}
