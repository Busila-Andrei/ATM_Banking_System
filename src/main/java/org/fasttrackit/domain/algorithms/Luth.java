package org.fasttrackit.domain.algorithms;

public class Luth {

    /*
        Verificarea algoritmului Luth
        Se scoate din numarul bancar ultima cifra (cifra de verificare)
        Cifrele de pe poziti impare se inmultesc cu 2, daca numarul rezultat este mai mare decat 9 atunci se v-a scadea 9
        Se calcureaza suma cifrelor
        Daca suma + cifra de verificare rezulta un numar ce este multiplu de 10 atunci numarul bancar este unul corect


        In cazul nostru numaratoarea incepe de la 0 deci se v-a calcula in functie de pozitiile pare
     */
    public static boolean verifLuth(String string) {
        if (string == null)
            return false;
        int[] arrays = new int[string.length()];
        int sum = 0;
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = Integer.parseInt(String.valueOf(string.charAt(i)));
        }
        for (int i = 0; i < arrays.length-1; i++) {
            if (i % 2 == 0)
                arrays[i] = arrays[i] * 2;

            if (arrays[i]  > 9)
                arrays[i] = arrays[i] - 9;
            sum = sum + arrays[i];
        }
        return (sum + arrays[arrays.length-1]) % 10 == 0;
    }
}
