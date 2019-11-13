package com.grape.lab.dao;

import com.grape.lab.model.AppUser;

import java.util.Optional;

public interface AppUserDao {

    Optional<AppUser> findByLogin(String login);

    void save(AppUser user);
}
