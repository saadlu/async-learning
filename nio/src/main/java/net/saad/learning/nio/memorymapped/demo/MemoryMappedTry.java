package net.saad.learning.nio.memorymapped.demo;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import net.saad.learning.nio.DontCareException;

public class MemoryMappedTry {

    public static void main(String[] args) {
        doit();
    }

    private static void doit() {
        try {
            MappedByteBuffer mappedByteBuffer = createMappedByteBuffer("sample.txt");
            ByteBuffer header = ByteBuffer.wrap("this is how we start\n".getBytes());
            writeOut(header, mappedByteBuffer);
        } catch (IOException e) {
            throw new DontCareException(e);
        }
    }

    private static void writeOut(ByteBuffer ...byteBuffer) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream("out.txt");
                FileChannel outChaneel = fileOutputStream.getChannel()) {

            while(outChaneel.write(byteBuffer) > 0) {

            }
        }
    }

    private static MappedByteBuffer createMappedByteBuffer(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
                FileChannel fileChannel = fis.getChannel()) {
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
        }
    }

}
