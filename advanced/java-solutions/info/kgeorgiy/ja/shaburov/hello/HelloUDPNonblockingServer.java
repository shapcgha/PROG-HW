package info.kgeorgiy.ja.shaburov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPNonblockingServer implements HelloServer {
    private ExecutorService worker;
    private DatagramChannel channel;
    private Selector selector;

    public static void main(final String[] args) {
        try {
            final int port = Integer.parseInt(args[0]);
            final int threads = Integer.parseInt(args[1]);
            new HelloUDPNonblockingServer().start(port, threads);
        } catch (final Exception e) {
            System.err.println("Usage: [int : port] [int : threads]");
        }
    }

    @Override
    public void start(final int port, final int threads) {
        try {
            final SocketAddress socket = new InetSocketAddress(port);
            selector = Selector.open();
            channel = HelloUDPUtil.createChannel().bind(socket);
            channel.register(selector, SelectionKey.OP_READ, new HelloUDPUtil.Attachment(channel.socket().getSendBufferSize()));
        } catch (final IOException e) {
            return;
        }
        worker = Executors.newSingleThreadExecutor();
        worker.submit(() -> {
            while (!Thread.interrupted() && selector.isOpen() && channel.isOpen()) {
                try {
                    HelloUDPUtil.checkSelect(selector, SelectionKey.OP_READ);
                    for (final Iterator<SelectionKey> selectionKey = selector.selectedKeys().iterator(); selectionKey.hasNext(); ) {
                        final SelectionKey key = selectionKey.next();
                        selectionKey.remove();
                        final DatagramChannel datagramChannel = (DatagramChannel) key.channel();
                        final HelloUDPUtil.Attachment keyAttachment = (HelloUDPUtil.Attachment) key.attachment();
                        final String request = keyAttachment.request;
                        ByteBuffer byteBuffer = ByteBuffer.allocate(keyAttachment.bufferSize);
                        if (key.isReadable()) {
                            keyAttachment.address = channel.receive(byteBuffer);
                            // :NOTE: Не параллельно
                            keyAttachment.request = "Hello, " + new String(byteBuffer.array(), StandardCharsets.UTF_8).trim();
                            if (datagramChannel.isOpen()) {
                                key.interestOps(SelectionKey.OP_WRITE);
                            }
                        } else if (key.isWritable()) {
                            byteBuffer = ByteBuffer.wrap(Objects.requireNonNull(request).getBytes(StandardCharsets.UTF_8));
                            try {
                                datagramChannel.send(byteBuffer, keyAttachment.address);
                            } catch (final IOException e) {
                                System.err.println("Error while writing handle " + e.getMessage());
                            }
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void close() {
        try {
            selector.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        worker.shutdownNow(); // :NOTE: NPE
        try {
            channel.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }
}
