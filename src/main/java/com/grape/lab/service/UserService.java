package com.grape.lab.service;

import com.grape.lab.service.impl.DefaultUserService;

public interface UserService {

    DefaultUserService.SignInResult signIn(String login, String password);
}
