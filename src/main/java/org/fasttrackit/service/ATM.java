package org.fasttrackit.service;

import org.fasttrackit.controller.StdinController;
import org.fasttrackit.controller.UserInputController;
import org.fasttrackit.domain.Account;
import org.fasttrackit.domain.GeneratesCard;
import org.fasttrackit.domain.algorithm.MOD97;
import org.fasttrackit.persistance.DataBase;

public class ATM {
    DataBase dataBase;
    UserInputController userInputController = new StdinController();

    public ATM(DataBase dataBase){
        this.dataBase = dataBase;
    }

    public void menuStart(){
        boolean isExit = false;
        while (!isExit) {
            String choice = userInputController.inputChoiceInMenuStart();
            System.out.println();
            switch (choice) {
                case "1":
                    createCard();
                    break;
                case "2":
                    isExit=login();
                    break;
                case "0":
                    System.out.println("Good day!");
                    isExit = true;
                    dataBase.close();
                    break;
                default:
                    System.out.println("You entered the wrong value. Please try again.\n");
            }
        }
    }

    public void createCard(){
        Account account = GeneratesCard.create(dataBase);
        System.out.println("Your card has been created");
        System.out.printf("IBAN: %s\n",account.getIban());
        System.out.printf("NUMBER: %s\n",account.getCard().getNumber());
        System.out.printf("PIN: %s\n",account.getCard().getPin());
        dataBase.insertNewCard(account.getIban(),account.getCard().getNumber(), account.getCard().getPin(), account.getBalance());
        System.out.println();
    }

    public boolean login() {
        int key = 0;
        String inputNumber;
        do {
            inputNumber = userInputController.inputNumberForLogin();
            System.out.println();
        }while (!dataBase.verifyNumber(inputNumber));
        do {

            String inputPin = userInputController.inputPinForLogin();

            System.out.println();
            if (dataBase.verifyLogin(inputNumber, inputPin)) {
                if (dataBase.verifyLock(inputNumber)) {
                    System.out.println("You have successfully logged in!\n");
                    return menuLogin(dataBase.selectAccount(inputNumber));
                } else {
                    System.out.println("Your account is locked.\n");
                    return false;
                }
            } else {
                System.out.println("Wrong card number or PIN!\n");
                key++;
            }
        } while (key < 3);
        if (key==3) {
            System.out.println("Your account has been blocked.\n");
            dataBase.setLock(inputNumber);
        }
        return false;
    }

    public boolean menuLogin(Account account){
        boolean isExit = false;
        menuLogin : for (;;) {
            String choice = userInputController.inputChoiceInMenuLogin();
            System.out.println();
            switch (choice) {
                case "1":
                    printBalance(account);
                    break;
                case "2":
                    addIncome(account);
                    break;
                case "3":
                    transferMoney(account);
                    break;
                case "4":
                    closeAccount(account);
                    break menuLogin;
                case "5":
                    break menuLogin;
                case "6":
                    changePin(account);
                    break menuLogin;
                case "0":
                    System.out.println("Good day!");
                    isExit = true;
                    dataBase.close();
                    break menuLogin;
                default:
                    System.out.println("You entered the wrong value. Please try again.\n");
            }
        }
        return isExit;
    }

    public void printBalance(Account account){
        System.out.println("\nBalance: " + dataBase.selectBalance(account) + "$\n");
    }

    public void addIncome(Account account){
        double income = userInputController.inputAddIncome();
        dataBase.insertIncome(account,income);
        account.setBalance(dataBase.selectBalance(account));
        System.out.println();
    }

    public void transferMoney(Account account){
        String ibanToSend = userInputController.inputIbanForSendMoney();
        double money;
        if (MOD97.verifyMOD97_10(ibanToSend)){
            if (dataBase.verifyExistAccountForSendMoney(ibanToSend)) {
                money = userInputController.inputMoneyForSend();
                if (account.getBalance() >= money) {
                    dataBase.transferMoney(account.getCard().getNumber(), ibanToSend, money);
                    account.setBalance(account.getBalance() - money);
                    System.out.println("Success!\n");
                } else System.out.println("Not enough money!\n");
            } else System.out.println("Such a card does not exist.\n");
        }else System.out.println("Probably you made mistake in the card number. Please try again!\n");
    }

    public void closeAccount(Account account){
        dataBase.deleteAccount(account);
        System.out.println("The account has been closed!");
    }

    public void changePin(Account account){
        String pin = userInputController.inputNewPin();
            dataBase.changePin(account,pin);
            account.getCard().setPin(pin);
            System.out.println("The pin has been changed.\n");
    }
}
