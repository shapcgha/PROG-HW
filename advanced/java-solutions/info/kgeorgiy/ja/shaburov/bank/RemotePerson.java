package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemotePerson extends AbstractPerson<RemoteAccount> {

    /**
     * Constructor of {@link LocalPerson} of name, surname, passportId and map of accounts
     *
     * @param name       name of {@link LocalPerson}
     * @param surname    surname of {@link LocalPerson}
     * @param passportId passportId of {@link LocalPerson}
     * @param port   map of accounts of {@link LocalPerson}
     */
    protected RemotePerson(final String name, final String surname, final String passportId, final int port) throws RemoteException{
        super(name, surname, passportId);
        UnicastRemoteObject.exportObject(this, port);
    }
}
