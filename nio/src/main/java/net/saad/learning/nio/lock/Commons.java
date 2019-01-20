package net.saad.learning.nio.lock;

import java.nio.ByteBuffer;

import net.saad.learning.nio.DontCareException;

public class Commons {

    static final int INDEX_COUNT = 5;
    static final String FILENAME = "temp.txt";

    static final int SIZE_OF_INT = 4;
    static final int SIZE_OF_BUFFER = INDEX_COUNT * SIZE_OF_INT;

    static int lastLineLen = 0;

    private Commons() {
    }

    static ByteBuffer createBuffer() {
        return ByteBuffer.allocate(SIZE_OF_BUFFER);
    }

    static void println(String msg) {
        System.out.print("\r");
        System.out.print(msg);

        for (int i = msg.length(); i < lastLineLen; i++) {
            System.out.print(" ");
        }

        System.out.print("\r");
        System.out.flush();
        lastLineLen = msg.length();
    }

    static void sleep(int amount) {
        try {
            Thread.sleep(amount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DontCareException(e);
        }
    }
}
