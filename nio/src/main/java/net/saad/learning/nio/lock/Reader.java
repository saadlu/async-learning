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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import net.saad.learning.nio.DontCareException;

public class Reader {

    private static ByteBuffer byteBuffer = Commons.createBuffer();
    private static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    private static int count = 0;

    public static void main(String[] args) {
        printIntegersFromFile(FILENAME);
    }

    static void printIntegersFromFile(String filename) {
        try (RandomAccessFile raf = new RandomAccessFile(filename, "r");
                FileChannel channel = raf.getChannel()) {

            printIntegersFromFilechannelWithLocking(channel);

        } catch (IOException e) {
            throw new DontCareException(e);
        }
    }

    private static void printIntegersFromFilechannelWithLocking(FileChannel channel)
            throws IOException {

        while (true) {
            FileLock lock = channel.lock(0, SIZE_OF_BUFFER, true);

            readIntegersFromChannelToBuffer(channel);
            printIntergersInBuffer();
            Commons.sleep(1000);

            lock.release();
        }
    }

    private static void readIntegersFromChannelToBuffer(FileChannel fileChannel) {
        clearBuffer();
        readToBufferIgnoringException(fileChannel);
    }

    private static void clearBuffer() {
        byteBuffer.clear();
    }

    private static void readToBufferIgnoringException(FileChannel fileChannel) {
        try {
            fileChannel.read(byteBuffer, 0);
        } catch (IOException e) {
            throw new DontCareException(e);
        }
    }

    private static void printIntergersInBuffer() {
        String intergers = createStringFromIntegersInBuffer();
        println(count + ": " + intergers);
        count++;
    }

    private static String createStringFromIntegersInBuffer() {
        return IntStream.range(0, INDEX_COUNT)
                .mapToObj(intBuffer::get)
                .collect(Collectors.toList()).toString();
    }

}
