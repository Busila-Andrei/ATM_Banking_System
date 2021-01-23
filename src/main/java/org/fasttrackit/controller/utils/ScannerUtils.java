package org.fasttrackit.controller.utils;

import java.util.Scanner;

public class ScannerUtils {
    static final Scanner SCANNER = new Scanner(System.in);

    public static int nextIntAndMoveToNextLine() {
        int integer = SCANNER.nextInt();
        SCANNER.nextLine();
        return integer;
    }

    public static double nextDoubleAndMoveToNextLine() {
        double doub = SCANNER.nextDouble();
        SCANNER.nextLine();
        return doub;
    }

    public static String nextLine() {
        return SCANNER.nextLine();
    }
}
