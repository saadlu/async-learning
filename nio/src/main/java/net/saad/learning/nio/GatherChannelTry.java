package net.saad.learning.nio;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class GatherChannelTry {

    public static final Random RANDOM = new Random();
    private static String[] col1 =
            { "Aggregate", "Enable", "Leverage", "Facilitate", "Synergize", "Repurpose",
                    "Strategize", "Reinvent", "Harness" };
    private static String[] col2 =
            { "cross-platform", "best-of-breed", "frictionless", "ubiquitous", "extensible",
                    "compelling", "mission-critical", "collaborative", "integrated" };
    private static String[] col3 =
            { "methodologies", "infomediaries", "platforms", "schemas", "mindshare", "paradigms",
                    "functionalities", "web services", "infrastructures" };

    public static void main(String[] args) throws Exception {

        try (FileOutputStream fileOutputStream = new FileOutputStream("somefile.txt");
                GatheringByteChannel channel = fileOutputStream.getChannel()) {

            ByteBuffer[] byteBuffers = randomStuff();
            while(channel.write(byteBuffers) > 0) {
                // empty
            }

            System.out.println("done");
        }

    }

    private static ByteBuffer[] randomStuff() throws UnsupportedEncodingException {
        return IntStream.range(0, 10)
                .mapToObj(i -> Arrays.asList(pickRandom(col1, " "),
                        pickRandom(col2, " "),
                        pickRandom(col3, System.lineSeparator()))
                )
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
                .toArray(new ByteBuffer[30]);
    }

    private static ByteBuffer pickRandom(String[] source, String sep) {

        try {
            String someString = source[RANDOM.nextInt(source.length)];
            int total = someString.length() + sep.length();

            ByteBuffer buffer = ByteBuffer.allocate(total);

            buffer.put(someString.getBytes("US-ASCII"));
            buffer.put(sep.getBytes("US-ASCII"));

            buffer.flip();

            return buffer;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
