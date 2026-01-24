package org.excelevate.milleniumbank.service;

public class BankingService {
    @SYZSdfasdf
    LoginService loginSerice;
    AccountService accountService;

    public BankingService() {
        loginSerice = new LoginServiceImpl();
        accountService = new AccountService();

    }

    public void showWelcomScreen() {
        while (true) {
            try {
                if (!loginSerice.isLoggedIn()) {
                    loginSerice.showAuthMenu();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

}
