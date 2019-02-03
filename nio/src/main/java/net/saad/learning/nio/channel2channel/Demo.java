package net.saad.learning.nio.channel2channel;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import net.saad.learning.nio.DontCareException;

/**
 * Channel 2 Channel demo
 *
 * Compare this with {@link net.saad.learning.nio.ChannelsTry}
 */

public class Demo {
    public static void main(String[] args) {
        doIt();
    }

    private static void doIt() {
        try (FileInputStream fileInputStream = new FileInputStream("sample.txt");
                FileChannel fileChannel = fileInputStream.getChannel()) {

            cat(fileChannel);

        } catch (IOException ex) {
            throw new DontCareException(ex);
        }
    }

    private static void cat(FileChannel fileChannel) throws IOException {
        try(WritableByteChannel to = Channels.newChannel(System.out)){
            fileChannel.transferTo(0, fileChannel.size(), to);
        }
    }
}
