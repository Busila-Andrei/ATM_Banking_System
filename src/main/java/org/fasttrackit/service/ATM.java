package org.fasttrackit.service;

import org.fasttrackit.controller.StdinController;
import org.fasttrackit.controller.UserInputController;
import org.fasttrackit.domain.Account;
import org.fasttrackit.domain.Generator;
import org.fasttrackit.domain.MOD97_10;
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
            int choice = userInputController.inputChoiceInMenuStart();
            System.out.println();
            switch (choice) {
                case 1:
                    createCard();
                    break;
                case 2:
                    isExit=login();
                    break;
                case 0:
                    System.out.println("Bye!");
                    isExit = true;
                    dataBase.close();
                    break;
            }
        }
    }

    public void createCard(){
        Account account = Generator.create(dataBase);
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getCard().getNumber());
        System.out.println("Your card PIN:");
        System.out.println(account.getCard().getPin());
        dataBase.insertNewCard(account.getIban(),account.getCard().getNumber(), account.getCard().getPin(), account.getBalance());
        System.out.println();
    }

    public boolean login() {
        int key = 0;
        String inputNumber;
        do {
            inputNumber = userInputController.inputNumberForLogin();
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
            int choice = userInputController.inputChoiceInMenuLogin();
            System.out.println();
            switch (choice) {
                case 1:
                    printBalance(account);
                    break;
                case 2:
                    addIncome(account);
                    break;
                case 3:
                    transferMoney(account);
                    break;
                case 4:
                    closeAccount(account);
                    break menuLogin;
                case 5:
                    break menuLogin;
                case 6:
                    if (changePin(account))
                        break menuLogin;
                    else break ;
                case 0:
                    System.out.println("Bye!");
                    isExit = true;
                    dataBase.close();
                    break menuLogin;
            }
        }
        return isExit;
    }

    public void printBalance(Account account){
        System.out.println("\nBalance: " + dataBase.selectBalance(account) + "\n");
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
        if (MOD97_10.verifyMOD97_10(ibanToSend)){
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

    public boolean changePin(Account account){
        String pin = userInputController.inputNewPin();
        if(dataBase.existPin(pin)){
            dataBase.changePin(account,pin);
            System.out.println("The pin has been changed.\n");
            return true;
        }else System.out.println("The pin is already in use.\n");
        return false;
    }
}
