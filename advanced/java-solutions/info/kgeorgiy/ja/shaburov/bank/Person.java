package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * {@link Remote} interface for Person;
 */
public interface Person extends Remote {
    String getName() throws RemoteException;

    String getSurname() throws RemoteException;

    String getPassportId() throws RemoteException;

    Set<String> getAccountsId() throws RemoteException;

    Account getAccountById(String id) throws RemoteException;

}
