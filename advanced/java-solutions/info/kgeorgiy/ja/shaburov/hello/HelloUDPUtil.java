package info.kgeorgiy.ja.shaburov.hello;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class HelloUDPUtil {
    static class Attachment {
        public String prefix;
        public int numberRequest;
        public String request;
        public int limit;
        int bufferSize;
        public SocketAddress address;

        Attachment(final String prefix, final int limit, final int bufferSize) {
            this.prefix = prefix;
            this.limit = limit;
            this.numberRequest = 0;
            this.bufferSize = bufferSize;
        }

        Attachment(final int bufferSize) {
            this.bufferSize = bufferSize;
        }
    }

    public static void checkSelect(final Selector selector, final int mode) throws IOException {
        if (selector.select(5) == 0) { // :NOTE: 5
            selector.keys().forEach(selectionKey -> selectionKey.interestOps(mode));
        }
    }

    public static DatagramChannel createChannel() throws IOException{
        final DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        return channel;
    }
}
