package net.saad.learning.async.socketchannel.trial;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import net.saad.learning.CantCareException;
import net.saad.learning.async.utility.StdPrintUtility;
import net.saad.learning.async.utility.StdReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocketConnectTry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketConnectTry.class);

    public static void main(String[] args) {
        doIt();
    }

    private static void doIt() {
        connectToServer(new InetSocketAddress("localhost", 8050));
    }

    private static void connectToServer(InetSocketAddress address) {
        try (SocketChannel socketChannel = SocketChannel.open()) {
            configureToNonBlock(socketChannel);
            System.out.println("initiating connection");
            makeConnection(socketChannel, address);

            while (!socketChannel.finishConnect()) {
                System.out.println("doing something useful");
            }

            System.out.println("connection established");
            echoWithSocket(socketChannel);

        } catch (IOException e) {
            LOGGER.error("Socket open exception", e);
        }
    }

    private static void configureToNonBlock(SocketChannel socketChannel) {
        try {
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            LOGGER.error("Socket configuration exception", e);
        }
    }

    private static void makeConnection(SocketChannel socketChannel, InetSocketAddress address) {
        try {
            socketChannel.connect(address);
        } catch (IOException e) {
            LOGGER.error("Socket connecting exception", e);
        }
    }

    private static void echoWithSocket(SocketChannel socketChannel) {
        try (StdReader stdReader = new StdReader()) {
            echoWithSocket(socketChannel, stdReader);
        } catch (IOException e) {
            LOGGER.error("Error closing StdReader", e);
        }
    }

    private static void echoWithSocket(SocketChannel socketChannel, StdReader stdReader) {
        String userInput = getUserInput(stdReader);
        while (userInput != null) {
            writeToSocket(socketChannel, userInput);
            echoResponse(socketChannel);
            userInput = getUserInput(stdReader);
        }

        StdPrintUtility.printlnOnScreen("\nclosing...");
        closeSocketChannel(socketChannel);
    }

    private static String getUserInput(StdReader stdReader) {
        try {
            return stdReader.promptUser("Type something to server: ");
        } catch (IOException e) {
            LOGGER.error("Error getting user input", e);
            throw new CantCareException(e);
        }
    }

    private static void writeToSocket(SocketChannel socketChannel, String userInput) {
        ByteBuffer bufferToWrite = ByteBuffer.wrap(userInput.getBytes());
        writeToSocket(socketChannel, bufferToWrite);
    }

    private static void writeToSocket(SocketChannel socketChannel, ByteBuffer bufferToWrite) {
        try {
            do {
                socketChannel.write(bufferToWrite);
            } while (bufferToWrite.hasRemaining());
        } catch (IOException e) {
            LOGGER.error("Write to socket", e);
        }
    }

    private static boolean echoResponse(SocketChannel socketChannel) {
        ByteBuffer buffer = readFromSocket(socketChannel);
        if (buffer != null) {
            StdPrintUtility.printlnFormatted("got from server: %s", new String(buffer.array()));
            return true;
        } else {
            return false;
        }
    }

    private static ByteBuffer readFromSocket(SocketChannel socketChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        int amountRead = readToBuffer(socketChannel, buffer);

        return amountRead > 0 ? (ByteBuffer) buffer.flip() : null;
    }

    private static int readToBuffer(SocketChannel socketChannel, ByteBuffer allocate) {
        int amountRead;
        do {
            amountRead = read(socketChannel, allocate);
        } while (amountRead == 0);
        return amountRead;
    }

    private static int read(SocketChannel socketChannel, ByteBuffer allocate) {
        try {
            return socketChannel.read(allocate);
        } catch (IOException e) {
            LOGGER.error("error reading socket", e);
            throw new CantCareException(e);
        }
    }

    private static void closeSocketChannel(SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            LOGGER.error("error closing socket", e);
        }
    }
}
