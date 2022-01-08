package com.fundamentos.springboot.fundamentos.caseuse;

import com.fundamentos.springboot.fundamentos.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class CreateUser {
    private UserService userService;

    public CreateUser(UserService userService) {
        this.userService = userService;
    }
}
