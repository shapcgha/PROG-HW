package info.kgeorgiy.ja.shaburov.bank;

import java.io.Serializable;

/**
 * {@link Serializable} implementation of {@link Account}
 */
public class LocalAccount extends AbstractAccount implements Serializable {
    /**
     * Constructor {@link LocalAccount} of id and amount
     *
     * @param id     of {@link LocalAccount}
     * @param amount of {@link LocalAccount}
     */
    LocalAccount(final String id, final int amount) {
        super(id, amount);
    }
}
