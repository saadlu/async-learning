package net.saad.learning.nio;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileChannelTry {

    public static void main(String[] args) throws Exception {
        try (RandomAccessFile file = new RandomAccessFile("myfile", "rw");
                FileChannel fileChannel = file.getChannel();) {

            file.seek(100);

            System.out.println(file.getFilePointer());
            System.out.println(fileChannel.position());

            fileChannel.position(1000);

            System.out.println(file.getFilePointer());
            System.out.println(fileChannel.position());
        }
    }
}
