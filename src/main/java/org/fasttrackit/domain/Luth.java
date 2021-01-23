package org.fasttrackit.domain;

public class Luth {
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
