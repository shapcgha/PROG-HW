package info.kgeorgiy.ja.shaburov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteBank extends UnicastRemoteObject implements Bank {
    private final int port;
    private final Map<String, RemotePerson> persons = new ConcurrentHashMap<>();

    protected RemoteBank(final int port) throws RemoteException {
        super(port);
        this.port = port;
    }

    /**
     * Creates new {@link Account} account to {@link Person}person
     *
     * @param id     account id
     * @param person person whose account is
     * @return link to {@link RemoteAccount} Account
     * @throws RemoteException if problem with communication
     */
    @Override
    public Account createAccount(final String id, final Person person) throws RemoteException {
        if (id != null && person != null) {
            final String personId = person.getPassportId() + ":" + id;
            final RemotePerson remotePerson = persons.get(person.getPassportId());
            if (!remotePerson.accounts.containsKey(id)) {
                final RemoteAccount account = new RemoteAccount(personId, port);
                remotePerson.accounts.put(personId, account);
                return account;
            } else {
                return remotePerson.accounts.get(id);
            }
        }
        return null;
    }

    /**
     * gets {@link Account} account to {@link Person}person
     *
     * @param id     account id
     * @param person person whose account is
     * @return link to {@link RemoteAccount} Account
     * @throws RemoteException if problem with communication
     */
    @Override
    public Account getAccount(String id, final Person person) throws RemoteException {
        if (id != null && person != null) {
            final RemotePerson remotePerson = persons.get(person.getPassportId());
            id = person.getPassportId() + ":" + id;
            final Account account = remotePerson.accounts.get(id);
            if (account != null) {
                if (person instanceof LocalPerson) {
                    return person.getAccountById(id);
                } else {
                    return account;
                }
            }
        }
        return null;
    }


    /**
     * Creates new {@link Person} person with his name surname and passportId
     *
     * @param name       name of person
     * @param surname    surname of person
     * @param passportId passportId of person
     * @return link to {@link RemotePerson} Account
     * @throws RemoteException if problem with communication
     */
    @Override
    public Person createPerson(final String name, final String surname, final String passportId) throws RemoteException {
        if (name != null && surname != null && passportId != null) {
            final RemotePerson person = new RemotePerson(name, surname, passportId, port);
            persons.put(passportId, person); // :NOTE: Обнуление долгов
            return person;
        } else {
            return null;
        }
    }

    /**
     * gets {@link Person} person with his passportId
     *
     * @param passportId passportId of person
     * @return link to {@link RemotePerson} Account
     * @throws RemoteException if problem with communication
     */
    @Override
    public RemotePerson getRemotePersonByPassport(final String passportId) throws RemoteException {
        if (passportId == null) {
            throw new NullPointerException();
        }
        return persons.get(passportId);
    }


    /**
     * gets {@link Person} person with his passportId
     *
     * @param passportId passportId of person
     * @return {@link LocalPerson} Account
     * @throws RemoteException if problem with communication
     */
    @Override
    public Person getLocalPersonByPassport(final String passportId) throws RemoteException {
        final RemotePerson person = getRemotePersonByPassport(passportId);
        if (person == null) {
            return null;
        }

        final Map<String, LocalAccount> personAccounts = new HashMap<>();
        person.accounts.forEach((key, account) ->
                personAccounts.put(key, new LocalAccount(account.getId(), account.getAmount())));
        return new LocalPerson(person.getName(), person.getSurname(), person.getPassportId(), personAccounts);
    }
}
