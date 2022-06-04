 package net.saad.learning.async.socketchannel.trial;

 import java.io.IOException;
 import java.net.InetSocketAddress;
 import java.nio.ByteBuffer;
 import java.nio.channels.ClosedChannelException;
 import java.nio.channels.SelectableChannel;
 import java.nio.channels.ServerSocketChannel;
 import java.nio.channels.SocketChannel;
 import java.nio.charset.StandardCharsets;
 import java.util.ArrayList;
 import java.util.List;

 import net.saad.learning.CantCareException;
 import net.saad.learning.async.utility.StdPrintUtility;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

public class ServerSocketTry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketConnectTry.class);
    private static List<SocketChannel> socketChannels = new ArrayList<>();
    private static List<SocketChannel> deadSockets = new ArrayList<>();

    public static void main(String[] args) {
        doit();
    }

    private static void doit() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            configureToNonBlock(serverSocketChannel);
            bindToPort(serverSocketChannel, 8050);
            dealWithIncomingConnection(serverSocketChannel);
        } catch (IOException e) {
            LOGGER.error("Server socket open exception", e);
        }
    }

    private static void configureToNonBlock(SelectableChannel selectableChannel) {
        try {
            selectableChannel.configureBlocking(false);
        } catch (IOException e) {
            LOGGER.error("Socket non-blocking configuraiton exception", e);
        }
    }

    private static void bindToPort(ServerSocketChannel serverSocketChannel, int port) {
        try {
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
        } catch (IOException e) {
            LOGGER.error("Socket binding exception", e);
        }

    }

    @SuppressWarnings("squid:S2189")
    private static void dealWithIncomingConnection(ServerSocketChannel serverSocketChannel) {
        while (true) {
            sleepAWhile();
            acceptRequest(serverSocketChannel);
            dealWithConnectedClients();
        }
    }

    private static void acceptRequest(ServerSocketChannel serverSocketChannel) {
        LOGGER.info("Waiting for connections");
        SocketChannel socketChannel = acceptAClient(serverSocketChannel);

        if (socketChannel != null) {
            logSocket("Got a request from", socketChannel);
            configureToNonBlock(socketChannel);
            socketChannels.add(socketChannel);
        }
    }

    private static void logSocket(String prefix, SocketChannel socketChannel) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("{} {}", prefix, formatSocket(socketChannel));
        }
    }

    private static SocketChannel acceptAClient(ServerSocketChannel serverSocketChannel) {
        try {
            return serverSocketChannel.accept();
        } catch (IOException e) {
            LOGGER.error("Socket accept exception", e);
        }
        return null;
    }

    private static String formatSocket(SocketChannel socketChannel) {
        try {
            return socketChannel.getRemoteAddress().toString();
        } catch (IOException e) {
            throw new CantCareException(e);
        }
    }

    private static void sleepAWhile() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Thread sleeping exception", e);
        }
    }

    private static void dealWithConnectedClients() {
        socketChannels.forEach(ServerSocketTry::dealWithAClient);
        socketChannels.removeAll(deadSockets);
        deadSockets.clear();
    }

    private static void dealWithAClient(SocketChannel socketChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(100);
        int amountRead = readFromSocket(socketChannel, buffer);

        if (amountRead > 0) {
            buffer.flip();
            logSocketContent(socketChannel, buffer);
            writeToSocket(socketChannel, buffer);
        } else if (amountRead == -1) {
            deadSockets.add(socketChannel);
        }

    }

    private static int readFromSocket(SocketChannel socketChannel, ByteBuffer buffer) {
        if (!socketChannel.isConnected()) {
            StdPrintUtility.printlnOnScreen("no longer connected");
        }
        return readFromConnected(socketChannel, buffer);
    }

    private static void logSocketContent(SocketChannel socketChannel, ByteBuffer buffer) {
        try {
            if (LOGGER.isInfoEnabled()) {
                String content = new String(buffer.array(), StandardCharsets.UTF_8);
                buffer.rewind();
                LOGGER.info("Got {} from {}", content, socketChannel.getRemoteAddress());
            }
        } catch (IOException e) {
            throw new CantCareException(e);
        }

    }

    private static int readFromConnected(SocketChannel socketChannel, ByteBuffer buffer) {
        try {
            LOGGER.info("waiting to read from {}", socketChannel.getRemoteAddress());
            int amountRead = socketChannel.read(buffer);
            LOGGER.info("read {} from {}", amountRead, socketChannel.getRemoteAddress());
            return amountRead;

        } catch (ClosedChannelException e) {
            LOGGER.info("Socket closed", e);
            return -1;
        } catch (IOException e) {
            LOGGER.error("Socket reading exception", e);
            throw new CantCareException(e);
        }
    }

    private static void writeToSocket(SocketChannel socketChannel, ByteBuffer byteBuffer) {
        try {
            do {
                byteBuffer.rewind();
                socketChannel.write(byteBuffer);
            } while (byteBuffer.hasRemaining());
        } catch (IOException e) {
            LOGGER.error("Socket write exception", e);
        }
    }

}
