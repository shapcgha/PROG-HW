package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * {@link Remote} interface for Bank;
 */
public interface Bank extends Remote {

    Account createAccount(String id, Person person) throws RemoteException;

    Account getAccount(String id, Person person) throws RemoteException;

    Person createPerson(String name, String surname, String passportId) throws RemoteException;

    Person getRemotePersonByPassport(String passportId) throws RemoteException;

    Person getLocalPersonByPassport(String passportId) throws RemoteException;
}
