package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * {@link Remote} interface for Account;
 */
public interface Account extends Remote {
    String getId() throws RemoteException;

    int getAmount() throws RemoteException;

    void setAmount(int amount) throws RemoteException;
}
