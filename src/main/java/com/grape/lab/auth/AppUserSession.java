package com.grape.lab.auth;

import com.grape.lab.model.AppUser;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class AppUserSession {
    private AppUser user;
}
