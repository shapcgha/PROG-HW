package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.*;
import java.net.*;
import java.rmi.registry.LocateRegistry;

public final class Server {
    private final static int DEFAULT_PORT = 8888;
    private final static String HOST = "//localhost/bank";

    /**
     * Start {@link Server} on default host and port
     * @param args port or nothing
     */
    public static void main(final String... args) {
        final int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        try {
            LocateRegistry.createRegistry(1099);
            final Bank bank = new RemoteBank(port);
            Naming.rebind(HOST, bank);
            System.out.println("Server started");
        } catch (final RemoteException e) {
            System.out.println("Cannot export object: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } catch (final MalformedURLException e) {
            System.out.println("Malformed URL");
        }
    }
}
