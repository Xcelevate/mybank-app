package com.training.mybank;

import com.training.mybank.model.Account;
import com.training.mybank.service.BankingService;

import java.util.List;
import java.util.Scanner;

public class BankingApp {
    private static final BankingService service = new BankingService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to Java Modern Banking System ===");

        while (true) {
            try {
                if (!isLoggedIn()) {
                    showAuthMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static boolean isLoggedIn() {
        // This checks if a user session exists in the service layer
        return service.getCurrentUser() != null;
    }

    private static void showAuthMenu() throws Exception {
        System.out.println("\n1. Login\n2. Exit");
        System.out.print("Choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> {
                System.out.print("UserID: ");
                String uid = scanner.nextLine();
                System.out.print("Password: ");
                String pwd = scanner.nextLine();

                if (service.login(uid, pwd)) {
                    System.out.println("Login successful! Welcome, " + uid);
                } else {
                    System.out.println("Invalid credentials.");
                }
            }
            case "2" -> System.exit(0);
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void showMainMenu() throws Exception {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. View My Accounts");
        System.out.println("2. Create New Account");
        System.out.println("3. View Balance");
        System.out.println("4. Deposit Money");
        System.out.println("5. Withdraw Money");
        System.out.println("6. Transfer Money");
        System.out.println("7. Logout");
        System.out.print("Selection: ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> listAccounts();
            case "2" -> {
                System.out.print("Enter initial deposit amount: ");
                double amount = Double.parseDouble(scanner.nextLine());
                service.createAccount(amount);
                System.out.println("Account created successfully.");
            }
            case "3" -> {
                int id = promptAccountId();
                double bal = service.getBalance(id);
                System.out.println("Current Balance: $" + bal);
            }
            case "4" -> {
                int id = promptAccountId();
                System.out.print("Amount to deposit: ");
                double amt = Double.parseDouble(scanner.nextLine());
                service.deposit(id, amt);
                System.out.println("Deposit complete.");
            }
            case "5" -> {
                int id = promptAccountId();
                System.out.print("Amount to withdraw: ");
                double amt = Double.parseDouble(scanner.nextLine());
                service.withdraw(id, amt);
                System.out.println("Withdrawal complete.");
            }
            case "6" -> {
                System.out.print("From Account ID: ");
                int from = Integer.parseInt(scanner.nextLine());
                System.out.print("To Account ID: ");
                int to = Integer.parseInt(scanner.nextLine());
                System.out.print("Transfer Amount: ");
                double amt = Double.parseDouble(scanner.nextLine());
                service.transferFunds(from, to, amt);
                System.out.println("Transfer successful.");
            }
            case "7" -> service.logout();
            default -> System.out.println("Invalid option.");
        }
    }

    private static void listAccounts() throws Exception {
        List<Account> accounts = service.getMyAccounts();
        if (accounts.isEmpty()) {
            System.out.println("You have no active accounts.");
        } else {
            System.out.println("\nID\t| Balance");
            System.out.println("------------------");
            for (Account acc : accounts) {
                System.out.printf("%d\t| $%.2f%n", acc.accountId(), acc.balance());
            }
        }
    }

    private static int promptAccountId() {
        System.out.print("Enter Account ID: ");
        return Integer.parseInt(scanner.nextLine());
    }
}