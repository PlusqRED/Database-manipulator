package com.grape.lab.service.impl;

import com.grape.lab.auth.AppUserSession;
import com.grape.lab.dao.AppUserDao;
import com.grape.lab.model.AppUser;
import com.grape.lab.service.UserService;
import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefaultUserService implements UserService {

    public enum SignInResult {
        OK, INCORRECT_PASSWORD("Incorrect password!"), NOT_EXISTS("User not exists");

        private String errorMessage;

        SignInResult() {
        }

        SignInResult(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    private static final SHA3.Digest256 digest256 = new SHA3.Digest256();
    private AppUserDao appUserDao;
    private AppUserSession appUserSession;

    @Override
    public SignInResult signIn(String login, String password) {
        Optional<AppUser> optionalUser = appUserDao.findByLogin(login);
        if (!optionalUser.isPresent()) {
            return SignInResult.NOT_EXISTS;
        }
        AppUser user = optionalUser.get();
        String hashedPasswordFromDb = user.getPassword();

        String hashedPassword = Hex.toHexString(digest256.digest(password.getBytes()));
        boolean signInSuccess = hashedPassword.equals(hashedPasswordFromDb);
        if (signInSuccess) {
            appUserSession.setUser(user);
            return SignInResult.OK;
        } else {
            return SignInResult.INCORRECT_PASSWORD;
        }
    }

    @Autowired
    public void setAppUserSession(AppUserSession appUserSession) {
        this.appUserSession = appUserSession;
    }

    @Autowired
    public void setAppUserDao(AppUserDao appUserDao) {
        this.appUserDao = appUserDao;
    }
}
