package info.kgeorgiy.ja.shaburov.hello;

import info.kgeorgiy.java.advanced.hello.HelloServer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloUDPServer implements HelloServer {
    private ExecutorService worker;
    private DatagramSocket socket;

    public static void main(final String[] args) {
        try {
            final int port = Integer.parseInt(args[0]);
            final int threads = Integer.parseInt(args[1]);
            new HelloUDPServer().start(port, threads);
        } catch (final Exception e) { // :NOTE: Exception
            System.err.println("Usage: [int : port] [int : threads]");
        }
    }

    @Override
    public void start(final int port, final int threads) {
        try {
            socket = new DatagramSocket(port);
            worker = Executors.newFixedThreadPool(threads);
            for (int i = 0; i < threads; i++) {
                worker.execute(() -> {
                    try {
                        final int bufferSize = socket.getSendBufferSize();
                        final DatagramPacket packet = new DatagramPacket(new byte[bufferSize], bufferSize);
                        while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                            try {
                                packet.setLength(bufferSize);
                                socket.receive(packet);
                                final String answer = "Hello, " + new String(packet.getData(),
                                        packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
                                packet.setData(answer.getBytes(StandardCharsets.UTF_8), packet.getOffset(), answer.length());
                                socket.send(packet);
                            } catch (final IOException e) {
                                System.err.println("Error in sending/receiving answer " + e.getMessage());
                            }
                        }
                    } catch (final SocketException e) {
                        System.err.println("Can't connect to port " + e.getMessage());
                    }
                });
            }
        } catch (final SocketException e) {
            System.err.println("Can't connect to port " + e.getMessage());
        }
    }

    @Override
    public void close() {
        socket.close();
        worker.shutdownNow();
    }
}
