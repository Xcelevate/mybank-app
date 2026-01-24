package org.excelevate.milleniumbank.service;

import lombok.extern.java.Log;
import org.excelevate.milleniumbank.dao.UserDAO;
import org.excelevate.milleniumbank.exception.AuthenticationException;
import org.excelevate.milleniumbank.model.User;

import java.sql.SQLException;
import java.util.Scanner;


public class LoginServiceImpl implements LoginService {

    static Scanner scanner = new Scanner(System.in);
    boolean loggedIn = false;
    UserDAO userDAO;




    public LoginServiceImpl() {
        userDAO = new UserDAO();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }


    public void showAuthMenu() throws Exception {
        System.out.println("\n1. Login\n2. Exit");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.print("UserID: ");
                String uid = scanner.nextLine();
                System.out.print("Password: ");
                String pwd = scanner.nextLine();
                try {
                    if (login(uid, pwd)) {
                        System.out.println("Login successful! Welcome, " + uid);
                    } else {
                        System.out.println("Invalid credentials.");
                    }
                } catch (AuthenticationException e) {
                    System.out.println("Exception Occurred");
                    System.exit(0);
                }

            }
            case "2" -> System.exit(0);
            default -> System.out.println("Invalid choice.");
        }
    }

    public boolean login(String userId, String password) throws AuthenticationException {
        //loggedIn = true;

        // Get users id and password from DB
        try {
            User currentUser = userDAO.getUserById(userId);

            if (currentUser != null && userId.equals(currentUser.userId()) && password.equals(currentUser.password())) {
                return true;
            }
        } catch (SQLException e) {
            throw new AuthenticationException(e.getMessage(), e, "123");

        }

        return false;
    }
}
