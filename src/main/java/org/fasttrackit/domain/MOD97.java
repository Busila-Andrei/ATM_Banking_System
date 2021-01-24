package org.fasttrackit.domain;

public class MOD97 {

    // converteste fiecare litera a unui IBAN in cifre
    private static String convertNumber(String string) {
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (Character.isDigit(string.charAt(i))) {
                number.append(string.charAt(i));
            } else number.append((int) string.charAt(i) - 55);
        }
        return number.toString();
    }

    // verifica daca codul IBAN este corect
    // un cod IBAN dupa ce a fost transformat in cifre si impartit la 97 trebuie sa rezulte restul 1
    public static boolean verifyMOD97_10(String iban) {
        int res = 0;
        iban = convertNumber(deconstruir(iban));
        for (int i = 0; i < iban.length(); i++) {
            res = (res * 10 + (int) iban.charAt(i) - '0') % 97;
        }
        return res == 1;
    }


    // genereaza cifrele de verificare
    public static String checkSum(String num) {
        int res = 0;
        num = convertNumber(num);
        for (int i = 0; i < num.length(); i++) {
            res = (res * 10 + (int) num.charAt(i) - '0') % 97;
        }
        return String.valueOf((97 + 1) - res);
    }

    // muta codul tari si cifrele de verificare la capatul codului
    private static String deconstruir(String IBAN) {
        StringBuilder newIBAN = new StringBuilder();
        newIBAN.append(IBAN);
        newIBAN.append(newIBAN.substring(0, 4));
        newIBAN.delete(0, 4);
        return newIBAN.toString();
    }
}
