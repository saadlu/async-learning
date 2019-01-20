package net.saad.learning.nio.lock;

import static net.saad.learning.nio.lock.Commons.FILENAME;
import static net.saad.learning.nio.lock.Commons.INDEX_COUNT;
import static net.saad.learning.nio.lock.Commons.SIZE_OF_BUFFER;
import static net.saad.learning.nio.lock.Commons.println;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import net.saad.learning.nio.DontCareException;

public class Writer {

    private static ByteBuffer byteBuffer = Commons.createBuffer();
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();

    private static int count = 0;

    public static void main(String[] args) throws IOException {
        writeIntegersToFile(FILENAME);
    }

    static void writeIntegersToFile(String filename) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filename, "rw");
                FileChannel channel = raf.getChannel()) {

            writeIntegersToChannelWithLocking(channel);

        }
    }

    private static void writeIntegersToChannelWithLocking(FileChannel channel) throws IOException {
        for (; ; ) {
            FileLock lock = channel.lock(0, SIZE_OF_BUFFER, false);
            writeIntegersToChannel(channel);
            lock.release();
        }
    }

    private static void writeIntegersToChannel(FileChannel channel) {
        writeToBuffer();
        writeBufferToChannel(channel);
        logToStd();
    }

    private static void writeToBuffer() {
        byteBuffer.clear();
        for (int i = 0; i < INDEX_COUNT; i++) {
            intBuffer.put(i, count);
        }
    }

    private static void writeBufferToChannel(FileChannel channel) {
        try {
            channel.write(byteBuffer, 0);
        } catch (IOException e) {
            throw new DontCareException(e);
        }
    }

    private static void logToStd() {
        println("written: " + count);
        count++;
    }
}
