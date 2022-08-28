package info.kgeorgiy.ja.shaburov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;

public class HelloUDPNonblockingClient implements HelloClient {

    public static void main(final String[] args) {
        try {
            final String host = args[0];
            final int port = Integer.parseInt(args[1]);
            final String prefix = args[2];
            final int threads = Integer.parseInt(args[3]);
            final int requests = Integer.parseInt(args[4]);
            new HelloUDPNonblockingClient().run(host, port, prefix, threads, requests);
        } catch (final Exception e) {
            System.err.println("Usage: [string : host] [int : port] [string : prefix] [int : threads] [int : requests]");
        }
    }

    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        final SocketAddress socket = new InetSocketAddress(host, port);
        try (final Selector selector = Selector.open()) {
            for (int thread = 0; thread < threads; thread++) {
                try {
                    final DatagramChannel channel = HelloUDPUtil.createChannel().connect(socket);
                    channel.register(selector, SelectionKey.OP_WRITE, new HelloUDPUtil.Attachment(prefix + thread + "_",
                            requests, channel.socket().getReceiveBufferSize()));
                } catch (final IOException ignored) {
                }
            }
            while (!Thread.interrupted() && !selector.keys().isEmpty()) {
                HelloUDPUtil.checkSelect(selector, SelectionKey.OP_WRITE);
                for (final Iterator<SelectionKey> selectionKey = selector.selectedKeys().iterator(); selectionKey.hasNext(); ) {
                    final SelectionKey key = selectionKey.next();
                    selectionKey.remove();
                    final DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                    final HelloUDPUtil.Attachment keyAttachment = (HelloUDPUtil.Attachment) key.attachment();
                    final String request = keyAttachment.prefix + keyAttachment.numberRequest;
                    ByteBuffer byteBuffer = ByteBuffer.allocate(keyAttachment.bufferSize);
                    if (key.isReadable()) {
                        try {
                            datagramChannel.read(byteBuffer);
                            final String answer = new String(byteBuffer.array(), StandardCharsets.UTF_8).trim();
                            if (answer.contains(request)) {
                                System.out.println(answer);
                                keyAttachment.numberRequest++;
                                if (keyAttachment.numberRequest == keyAttachment.limit) {
                                    datagramChannel.close();
                                }
                            }
                        } catch (final IOException e) {
                            System.err.println("Error while reading handle " + e.getMessage());
                        }
                        if (datagramChannel.isOpen()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    } else if (key.isWritable()) {
                        byteBuffer = ByteBuffer.wrap(Objects.requireNonNull(request).getBytes(StandardCharsets.UTF_8));
                        try {
                            datagramChannel.write(byteBuffer);
                        } catch (final IOException e) {
                            System.err.println("Error while writing handle " + e.getMessage());
                        }
                        key.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (final IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
