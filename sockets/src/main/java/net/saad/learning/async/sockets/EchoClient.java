package net.saad.learning.async.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import net.saad.learning.async.utility.StdPrintUtility;
import net.saad.learning.async.utility.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoClient.class);

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 4444)) {
            echoWithServer(socket);
        }
    }

    private static void echoWithServer(Socket serverSocket) {
        StdPrintUtility.printlnOnScreen("connected to server");
        echo(serverSocket);
    }

    private static void echo(Socket serverSocket) {
        try (PrintWriter serverWriter = new PrintWriter(serverSocket.getOutputStream(), true)) {
            echoWithServer(serverSocket, serverWriter);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void echoWithServer(Socket serverSocket, PrintWriter toServer)
            throws IOException {

        try (BufferedReader serverReader = new BufferedReader(
                    new InputStreamReader(serverSocket.getInputStream()))) {
            echoWithServer(toServer, serverReader);
        }

    }

    private static void echoWithServer(PrintWriter toServer, BufferedReader serverReader)
            throws IOException {

        try(StdReader stdin = new StdReader()) {
            echoWithServer(toServer, serverReader, stdin);
        }
    }

    private static void echoWithServer(PrintWriter toServer, BufferedReader serverReader,
            StdReader stdin) throws IOException {

        String userInput = promptUser(stdin);
        while (userInput != null) {
            toServer.println(userInput);
            String serverResponse = serverReader.readLine();
            StdPrintUtility.printlnFormatted("server says: %s", serverResponse);
            userInput = promptUser(stdin);
        }
    }

    private static String promptUser(StdReader stdin) throws IOException {
        return stdin.promptUser("message to sent to server (Ctrl+D to end): ");
    }
}
