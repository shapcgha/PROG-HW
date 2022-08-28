package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * {@link UnicastRemoteObject} implementation of {@link Account}
 */
// :NOTE: Общий код
class RemoteAccount extends AbstractAccount {

    /**
     * Constructor {@link RemoteAccount} of id and amount
     *
     * @param id     of {@link RemoteAccount}
     * @param port of {@link RemoteAccount}
     */
    RemoteAccount(final String id, final int port) throws RemoteException {
        super(id);
        UnicastRemoteObject.exportObject(this, port);
    }
}
