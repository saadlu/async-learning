package net.saad.learning.async.sockets;

public class StdPrintUtility {

    private StdPrintUtility() {
        throw new AssertionError();
    }

    static void printlnFormatted(String format, Object... args) {
        String formattedString = String.format(format, args);
        printlnOnScreen(formattedString);
    }

    static void printlnOnScreen(String formattedString) {
        System.out.println(formattedString);
    }

    static void printOnScreen(String formattedString) {
        System.out.print(formattedString);
    }
}