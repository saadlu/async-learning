package net.saad.learning.async.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdReader implements AutoCloseable {

    private final BufferedReader stdin;

    public StdReader() {
        stdin = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void close() throws IOException {
        stdin.close();
    }

    public String promptUser(String prompt) throws IOException {
        StdPrintUtility.printOnScreen(prompt);
        return stdin.readLine();
    }
}
