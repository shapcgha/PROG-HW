package info.kgeorgiy.ja.shaburov.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * {@link Serializable} implementation of {@link Person}
 */
// :NOTE: Общий интерфейс
public class LocalPerson extends AbstractPerson<LocalAccount> implements Serializable {

    /**
     * Constructor of {@link LocalPerson} of name, surname, passportId and map of accounts
     *
     * @param name       name of {@link LocalPerson}
     * @param surname    surname of {@link LocalPerson}
     * @param passportId passportId of {@link LocalPerson}
     * @param accounts   map of accounts of {@link LocalPerson}
     */
    protected LocalPerson(String name, String surname, String passportId, Map<String, LocalAccount> accounts) {
        super(name, surname, passportId, accounts);
    }
}
