package net.saad.learning.nio.memorymapped.copyonwrite;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.stream.Stream;

import net.saad.learning.nio.DontCareException;
import org.apache.commons.io.FileUtils;

public class MemoryMappedVariousOptionsTry {

    private static class MemoryMappedBuffer {

        public final FileChannel.MapMode mapMode;

        public final MappedByteBuffer mappedByteBuffer;

        private MemoryMappedBuffer(FileChannel.MapMode mapMode,
                MappedByteBuffer mappedByteBuffer) {

            this.mapMode = mapMode;
            this.mappedByteBuffer = mappedByteBuffer;
        }


    }

    private static final int FIRST_MUTANT_POS = 12;
    private static final int HOLE_SIZE = (int) FileUtils.ONE_KB * 16;
    private static final int SECOND_MUTANT_POS = HOLE_SIZE + FIRST_MUTANT_POS;

    public static void main(String[] args) {
        try {
            doit();
        } catch (IOException ex) {
            throw new DontCareException(ex);
        }
    }

    private static void doit() throws IOException {
        File file = new File("mmpatest.txt");
        doit(file);
        Files.delete(file.toPath());
    }

    private static void doit(File file) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                FileChannel fileChannel = raf.getChannel()) {

            doit(fileChannel);
        }
    }

    private static void doit(FileChannel fileChannel) throws IOException {
        intialWrites(fileChannel);

        MemoryMappedBuffer readOnly = memoryMap(fileChannel, FileChannel.MapMode.READ_ONLY);
        MemoryMappedBuffer readWrite = memoryMap(fileChannel, FileChannel.MapMode.READ_WRITE);
        MemoryMappedBuffer privateBuffer = memoryMap(fileChannel, FileChannel.MapMode.PRIVATE);

        printMemoryMappedBuffers("initial writes", readOnly, readWrite, privateBuffer);

        write(privateBuffer.mappedByteBuffer, FIRST_MUTANT_POS, "cow  ");
        printMemoryMappedBuffers("first cow change", readOnly, readWrite, privateBuffer);

        write(readWrite.mappedByteBuffer, FIRST_MUTANT_POS, "r/w  ");
        write(readWrite.mappedByteBuffer, SECOND_MUTANT_POS, "r/w   ");
        printMemoryMappedBuffers("r/w change", readOnly, readWrite, privateBuffer);

        write(fileChannel, FIRST_MUTANT_POS, "chanl");
        write(fileChannel, SECOND_MUTANT_POS, "chanl");
        printMemoryMappedBuffers("Write by channel", readOnly, readWrite, privateBuffer);

        write(privateBuffer.mappedByteBuffer, FIRST_MUTANT_POS, "cow2 ");
        write(privateBuffer.mappedByteBuffer, SECOND_MUTANT_POS, "cow2  ");
        printMemoryMappedBuffers("Write by channel", readOnly, readWrite, privateBuffer);

        write(readWrite.mappedByteBuffer, FIRST_MUTANT_POS, "r/w2 ");
        write(readWrite.mappedByteBuffer, SECOND_MUTANT_POS, "r/w2  ");
        printMemoryMappedBuffers("r/w change", readOnly, readWrite, privateBuffer);
    }

    private static void intialWrites(FileChannel fileChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(100);

        putAndFlip(buffer, "This is the first part");
        write(fileChannel, buffer);
        putAndFlip(buffer, "This is the second part");

        write(fileChannel, buffer, HOLE_SIZE);
    }

    private static void putAndFlip(ByteBuffer buffer, String s) {
        buffer.clear();
        buffer.put(s.getBytes());
        buffer.flip();
    }

    private static void write(FileChannel fileChannel, ByteBuffer buffer) throws IOException {
        do {
            fileChannel.write(buffer);
        } while (buffer.hasRemaining());
    }

    private static void write(FileChannel fileChannel, ByteBuffer buffer, int offset)
            throws IOException {

        do {
            fileChannel.write(buffer, offset);
        } while (buffer.hasRemaining());
    }

    private static MemoryMappedBuffer memoryMap(FileChannel fileChannel,
            FileChannel.MapMode mapMode)
            throws IOException {

        return new MemoryMappedBuffer(mapMode, fileChannel.map(mapMode, 0, fileChannel.size()));
    }

    private static void printMemoryMappedBuffers(String intro,
            MemoryMappedBuffer... memoryMappedBuffers) {

        System.out.println(intro);
        System.out.println();
        Stream.of(memoryMappedBuffers)
                .forEach(MemoryMappedVariousOptionsTry::printOutput);
        System.out.println();
    }

    private static void printOutput(MemoryMappedBuffer memoryMappedBuffer) {
        String output = formatOutput(memoryMappedBuffer.mappedByteBuffer);
        System.out.println(String.format("%12s: %s", memoryMappedBuffer.mapMode, output));
    }

    private static String formatOutput(ByteBuffer buffer) {
        StringBuilder builder = new StringBuilder();
        int limit = buffer.limit();
        int nullCount = 0;

        for (int i = 0; i < limit; i++) {
            char c = (char) buffer.get(i);

            if (c == '\u0000') {
                nullCount++;
            } else {
                if (nullCount != 0) {
                    builder.append(String.format(" | [%10d nulls] | ", nullCount));
                    nullCount = 0;
                }

                builder.append(c);
            }
        }

        return builder.toString();
    }

    private static void write(MappedByteBuffer byteBuffer, int position, String content) {
        byteBuffer.position(position);
        byteBuffer.put(content.getBytes());
    }

    private static void write(FileChannel fileChannel, int pos, String content) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(100);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        fileChannel.write(byteBuffer, pos);
    }
}
