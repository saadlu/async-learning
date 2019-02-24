package net.saad.learning.async.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import net.saad.learning.async.utility.StdPrintUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoMultiClientServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoMultiClientServer.class);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(4444)) {
            listenAsEchoServer(serverSocket);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void listenAsEchoServer(ServerSocket serverSocket) {
        try  {
            handleRequest(serverSocket);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("squid:S2189")
    private static void handleRequest(ServerSocket serverSocket) throws IOException {
        for(;;) {
            StdPrintUtility.printlnOnScreen("listening as echo server");
            Socket clientSocket = serverSocket.accept();
            dealWithClientSeparately(clientSocket);
        }
    }

    private static void dealWithClientSeparately(Socket clientSocket) {
        new Thread(() -> {
            dealWithClient(clientSocket);
            closeClientSocket(clientSocket);
        }).start();
    }

    private static void closeClientSocket(Socket clientSocket) {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void dealWithClient(Socket clientSocket) {
        String clientId = resolvedClientId(clientSocket);
        StdPrintUtility.printlnFormatted("connected to a client, %s", clientId);
        dealWithClient(clientId, clientSocket);
    }

    private static String resolvedClientId(Socket clientSocket) {
        return String.format("%s(%d)",
                clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
    }

    private static void dealWithClient(String clientId, Socket clientSocket) {
        try (PrintWriter clientPrintWriter = new PrintWriter(clientSocket.getOutputStream(),
                true)) {

            dealWithClient(clientId, clientPrintWriter, clientSocket);

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static void dealWithClient(String clientId, PrintWriter clientWriter,
            Socket clientSocket) {

        try (BufferedReader fromClient = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()))) {

            dealWithClient(clientId, clientWriter, fromClient);

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    private static void dealWithClient(String clientId, PrintWriter clientWriter,
            BufferedReader clientReader) {

        try {

            echo(clientId, clientWriter, clientReader);

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    private static void echo(String clientId, PrintWriter clientWriter, BufferedReader clientReader)
            throws IOException {

        String lineFromClient = clientReader.readLine();

        while (lineFromClient != null) {
            StdPrintUtility.printlnFormatted("client (%s): %s", clientId, lineFromClient);
            clientWriter.println(lineFromClient);
            lineFromClient = clientReader.readLine();
        }

        StdPrintUtility.printlnFormatted("done with client, %s.", clientId);
    }

}
