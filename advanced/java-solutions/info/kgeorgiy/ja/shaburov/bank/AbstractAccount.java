package info.kgeorgiy.ja.shaburov.bank;

import java.io.Serializable;

public class AbstractAccount implements Account, Serializable {
    private final String id;
    private int amount;

    /**
     * Constructor {@link AbstractAccount} of id and amount
     * @param id of {@link AbstractAccount}
     * @param amount of {@link AbstractAccount}
     */
    AbstractAccount(final String id, final int amount) {
        this.id = id;
        this.amount = amount;
    }

    AbstractAccount(final String id) {
        this.id = id;
        this.amount = 0;
    }

    /**
     * Returns id of {@link AbstractAccount}
     * @return id of {@link AbstractAccount}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns amount of money of {@link AbstractAccount}. Method is synchronized
     * @return amount of money of {@link AbstractAccount}
     */
    @Override
    public synchronized int getAmount() {
        return amount;
    }

    /**
     * Change amount of money of {@link AbstractAccount}. Method is synchronized
     * @param amount new amount of money of {@link AbstractAccount}
     */
    @Override
    public synchronized void setAmount(final int amount) {
        this.amount = amount;
    }
}
