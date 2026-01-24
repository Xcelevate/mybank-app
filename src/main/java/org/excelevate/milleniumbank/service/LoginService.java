package org.excelevate.milleniumbank.service;

import org.excelevate.milleniumbank.exception.AuthenticationException;

public interface LoginService {

    public boolean isLoggedIn();
    public void showAuthMenu() throws Exception;
    public boolean login(String userId, String password) throws AuthenticationException;
}
