package net.saad.learning.async.utility;

public class StdPrintUtility {

    private StdPrintUtility() {
        throw new AssertionError();
    }

    public static void printlnFormatted(String format, Object... args) {
        String formattedString = String.format(format, args);
        printlnOnScreen(formattedString);
    }

    public static void printlnOnScreen(String formattedString) {
        System.out.println(formattedString);
    }

    public static void printOnScreen(String formattedString) {
        System.out.print(formattedString);
    }
}