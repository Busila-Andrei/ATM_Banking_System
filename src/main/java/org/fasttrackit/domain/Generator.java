package org.fasttrackit.domain;

import org.fasttrackit.persistance.DataBase;

import java.util.Random;

public class Generator {
    public static Account create(DataBase dataBase) {
        boolean nonUnique;
        String number;
        String pin;
        String iban;
        do {
            number = getNewNumber();
            pin = getNewPin();
            iban = getNewIBAN();
            nonUnique = dataBase.verifyExist(iban,number);
        } while (!nonUnique);
        return new Account(iban,new Card(number, pin));
    }

    private static String getNewNumber() {
        StringBuilder uuid;
        boolean nonUnique;

        do {
            uuid = new StringBuilder();
            uuid.append(getMII());
            uuid.append("00000");
            uuid.append(getAccountIdentifier());
            uuid.append(checkSum(uuid.toString()));
            nonUnique = Luth.verifLuth(uuid.toString());
        } while (!nonUnique);
        return uuid.toString();
    }

    //Major industries identifier
    //For banks it can be 4,5,6
    private static String getMII(){
        Random random = new Random();
        return String.valueOf(4 + random.nextInt(6-4+1));
    }

    //a randomly selected 9-digit number
    private static String getAccountIdentifier(){
        Random random = new Random();
        StringBuilder id = new StringBuilder(0);
        for (int i = 0; i < 9; i++){
            id.append(random.nextInt(10));
        }
        return id.toString();
    }

    //the last digit of the number
    private static String checkSum(String uuid){
        if (uuid == null)
            return "Eroare de generare!!!";
        int[] arrays = new int[uuid.length()];
        int sum = 0;
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = Integer.parseInt(String.valueOf(uuid.charAt(i)));
        }
        for (int i = 0; i < arrays.length; i++) {
            if (i % 2 == 0)
                arrays[i] = arrays[i] * 2;

            if (arrays[i]  > 9)
                arrays[i] = arrays[i] - 9;
            sum = sum + arrays[i];
        }
        for (int i = 0; i < 10; i++){
            if ((sum + i) % 10 == 0){
                return String.valueOf(i);
            }
        }
        return "Eroare de generare";
    }

    private static String getNewPin() {
        StringBuilder pin;
        int len = 4;
        Random random = new Random();
        pin = new StringBuilder();
        for (int i = 0; i < len; i++) {
            pin.append(random.nextInt(10));
        }
        return pin.toString();
    }

    public static String getNewIBAN(){
        String iban;
        Random random = new Random();
        boolean nonUnique;
        String countryCode = "RO";
        String verificationNumber;
        String bankCode = "INGB";
        StringBuilder accountNumber;
        StringBuilder verifyIBAN;

        do {
            accountNumber= new StringBuilder();
            verifyIBAN = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                accountNumber.append(random.nextInt(10));
            }
            verifyIBAN.append(bankCode);
            verifyIBAN.append(accountNumber);
            verifyIBAN.append(countryCode);
            verifyIBAN.append("00");
            verificationNumber = MOD97_10.checkSum(verifyIBAN.toString());

            iban = countryCode + verificationNumber + bankCode + accountNumber.toString();
            nonUnique = MOD97_10.verifyMOD97_10(iban);
        }while (!nonUnique);
        return iban;

    }
}
