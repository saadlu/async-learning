package net.saad.learning.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class ChannelsTry {

    public static final ByteBuffer BYTE_BUFFER = ByteBuffer.allocate(16 * 1024);

    public static void main(String[] args) throws IOException {
        try (ReadableByteChannel readableByteChannel = Channels.newChannel(System.in);
                WritableByteChannel writableByteChannel = Channels.newChannel(System.out)) {

            copy2(readableByteChannel, writableByteChannel);

        }
    }

    private static void copy1(ReadableByteChannel readableByteChannel,
            WritableByteChannel writableByteChannel) throws IOException {

        while (readableByteChannel.read(BYTE_BUFFER) != -1) {
            BYTE_BUFFER.flip();
            writableByteChannel.write(BYTE_BUFFER);
            BYTE_BUFFER.compact();
        }

        BYTE_BUFFER.flip();

        while (BYTE_BUFFER.remaining() > 0) {
            writableByteChannel.write(BYTE_BUFFER);
        }
    }

    private static void copy2(ReadableByteChannel readableByteChannel,
            WritableByteChannel writableByteChannel) throws IOException {

        while (readableByteChannel.read(BYTE_BUFFER) != -1) {
            BYTE_BUFFER.flip();

            while(BYTE_BUFFER.remaining() > 0) {
                writableByteChannel.write(BYTE_BUFFER);
            }
            BYTE_BUFFER.clear();
        }

    }
}
