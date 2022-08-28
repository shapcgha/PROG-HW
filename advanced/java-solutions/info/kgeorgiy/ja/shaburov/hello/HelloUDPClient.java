package info.kgeorgiy.ja.shaburov.hello;

import info.kgeorgiy.java.advanced.hello.HelloClient;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class HelloUDPClient implements HelloClient {
    public static void main(final String[] args) {
        try {
            final String host = args[0];
            final int port = Integer.parseInt(args[1]);
            final String prefix = args[2];
            final int threads = Integer.parseInt(args[3]);
            final int requests = Integer.parseInt(args[4]);
            new HelloUDPClient().run(host, port, prefix, threads, requests);
        } catch (final Exception e) { // :NOTE: Exception
            System.err.println("Usage: [string : host] [int : port] [string : prefix] [int : threads] [int : requests]");
        }
    }

    @Override
    public void run(final String host, final int port, final String prefix, final int threads, final int requests) {
        final SocketAddress socket = new InetSocketAddress(host, port);
        final ExecutorService worker = Executors.newFixedThreadPool(threads);
        IntStream.range(0, threads).forEach(threadNumber -> worker.submit(() -> {
            try (final DatagramSocket datagramSocket = new DatagramSocket()) {
                datagramSocket.setSoTimeout(1000); // :NOTE: Константа
                final int size = datagramSocket.getReceiveBufferSize();
                final DatagramPacket receive = new DatagramPacket(new byte[size], size);
                final DatagramPacket packet = new DatagramPacket(new byte[0], 0, socket);
                for (int requestNumber = 0; requestNumber < requests; requestNumber++) {
                    final String request = String.format("%s%d_%d", prefix, threadNumber, requestNumber);
                    packet.setData(request.getBytes(StandardCharsets.UTF_8));
                    while (true) {
                        try {
                            datagramSocket.send(packet);
                            receive.setLength(size);
                            datagramSocket.receive(receive);
                            final String answer = new String(receive.getData(),
                                    receive.getOffset(), receive.getLength(), StandardCharsets.UTF_8);
                            if (answer.contains(request)) {
                                System.out.println(answer);
                                break;
                            }
                        } catch (final SocketTimeoutException e) {
                            System.err.println("Socket timeout " + e.getMessage());
                        } catch (final IOException e) {
                            System.err.println("Error in sending/receiving request " + e.getMessage());
                        }
                    }
                }
            } catch (final SocketException e) {
                System.err.println("Error in socket work " + e.getMessage());
            }
        }));
        worker.shutdownNow();
        try {
            worker.awaitTermination(3, TimeUnit.MINUTES);
        } catch (final InterruptedException ignored) {
        }
    }
}
