package info.kgeorgiy.ja.shaburov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public final class Client {
    /**
     * Utility class.
     */
    private Client() {
    }

    /**
     * Start work of {@link Client} with {@link Server}
     * @param args not
     */
    public static void main(final String... args) throws RemoteException{
        LocateRegistry.createRegistry(1098);
        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        } catch (RemoteException e) {
            System.out.println("Remote Error " + e.getMessage());
            return;
        }
        try {
            String name = args[0];
            String surname = args[1];
            String passportId = args[2];
            String accountId = args[3];
            int amount = Integer.parseInt(args[4]);
            Person person = bank.getLocalPersonByPassport(passportId);
            Account account;
            if (person != null) {
                account = bank.getAccount(accountId, person);
                if (account != null) {
                    account = bank.createAccount(accountId, person);
                }
            } else {
                person = bank.createPerson(name, surname, passportId);
                account = bank.createAccount(accountId, person);
            }
            if (account == null) {
                System.err.println("Can't create account");
                return;
            }
            System.out.println("Account id: " + account.getId());
            System.out.println("Money: " + account.getAmount());
            System.out.println("Adding money");
            account.setAmount(account.getAmount() + amount);
            System.out.println("Money: " + account.getAmount());
        } catch (RemoteException e) {
            System.err.println("Remote error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Usage: [string : name] [string : surname] [string : passportId] [string : accountId] [int : amount]");
        }
    }
}
