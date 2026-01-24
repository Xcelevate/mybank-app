package org.excelevate.milleniumbank;

import org.excelevate.milleniumbank.service.BankingService;

public class MilleniumBank {

    public static void main(String[] args) {
        System.out.println("=== Welcome to Java Millenium Banking System ===");
        BankingService bankingService = new BankingService();
        bankingService.showWelcomScreen();

    }

}
