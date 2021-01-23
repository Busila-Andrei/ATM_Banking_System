package org.fasttrackit.controller;

import org.fasttrackit.controller.utils.ScannerUtils;

public class StdinController implements UserInputController{
    @Override
    public int inputChoiceInMenuStart() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
        return ScannerUtils.nextIntAndMoveToNextLine();
    }

    @Override
    public String inputNumberForLogin() {
        System.out.println("Enter your card number:");
        return ScannerUtils.nextLine();
    }

    @Override
    public String inputPinForLogin() {
        System.out.println("Enter your PIN:");
        return ScannerUtils.nextLine();
    }

    @Override
    public int inputChoiceInMenuLogin() {
        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("6. Change pin");
        System.out.println("0. Exit");
        return ScannerUtils.nextIntAndMoveToNextLine();
    }

    @Override
    public Double inputAddIncome() {
        System.out.println("Enter income:");
        return ScannerUtils.nextDoubleAndMoveToNextLine();
    }

    @Override
    public String inputIbanForSendMoney() {
        System.out.println("Transfer");
        System.out.println("Enter account iban:");
        return ScannerUtils.nextLine();
    }

    @Override
    public Double inputMoneyForSend() {
        System.out.println("Enter how much money you want to transfer:");
        return ScannerUtils.nextDoubleAndMoveToNextLine();
    }

    @Override
    public String inputNewPin() {
        System.out.println("Insert a new pin:");
        return ScannerUtils.nextLine();
    }
}
