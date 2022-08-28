package info.kgeorgiy.ja.shaburov.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractPerson<T extends Account> implements Person, Serializable {
    private final String name;
    private final String surname;
    private final String passportId;
    public final Map<String, T> accounts; // :NOTE: ??

    /**
     * Constructor of {@link AbstractPerson} of name, surname, passportId and map of accounts
     * @param name name of {@link AbstractPerson}
     * @param surname surname of {@link AbstractPerson}
     * @param passportId passportId of {@link AbstractPerson}
     * @param accounts map of accounts of {@link AbstractPerson}
     */
    protected AbstractPerson(String name, String surname, String passportId, Map<String, T> accounts) {
        this.name = name;
        this.surname = surname;
        this.passportId = passportId;
        this.accounts = accounts;
    }

    protected AbstractPerson(String name, String surname, String passportId) {
        this.name = name;
        this.surname = surname;
        this.passportId = passportId;
        this.accounts = new ConcurrentHashMap<>();
    }

    /**
     * Returns name of {@link AbstractPerson}
     * @return name of {@link AbstractPerson}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns surname of {@link AbstractPerson}
     * @return surname of {@link AbstractPerson}
     */
    @Override
    public String getSurname() {
        return surname;
    }

    /**
     * Returns passportId of {@link AbstractPerson}
     * @return passportId of {@link AbstractPerson}
     */
    @Override
    public String getPassportId() {
        return passportId;
    }

    /**
     * Returns account of {@link AbstractPerson} by it's id
     * @param id id of account
     * @return  account of {@link AbstractPerson}
     */
    @Override
    public synchronized T getAccountById(String id) {
        return accounts.get(passportId + ":" + id);
    }

    /**
     * Returns set of id of {@link AbstractPerson}'s accounts
     * @return set of id of {@link AbstractPerson}'s accounts
     */
    @Override
    public Set<String> getAccountsId() {
        return accounts.keySet();
    } // :NOTE: Можно удалять
}
