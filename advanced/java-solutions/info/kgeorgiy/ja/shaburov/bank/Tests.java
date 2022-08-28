package info.kgeorgiy.ja.shaburov.bank;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Tests {
    private static Bank bank;
    private final static int DEFAULT_PORT = 8888;
    private final static String HOST = "//localhost/bank";

    @BeforeClass
    public static void beforeClass() throws Exception {
        LocateRegistry.createRegistry(1099);
        Naming.rebind(HOST, new RemoteBank(DEFAULT_PORT));
        bank = (Bank) Naming.lookup(HOST);
    }

    @Test
    public void createGetPersonTest() throws RemoteException{
        for (int i = 1000; i < 1010; i++) {
            Assert.assertNull(bank.getRemotePersonByPassport(Integer.toString(i)));
            Assert.assertNull(bank.getLocalPersonByPassport(Integer.toString(i)));
            createPerson("name" + i, "surname" + i, Integer.toString(i));
            final Person person = bank.getRemotePersonByPassport(Integer.toString(i));
            testPerson(person, i);
            final Person localPerson = bank.getLocalPersonByPassport(Integer.toString(i));
            testPerson(localPerson, i);
        }
    }

    void testPerson(final Person localPerson, final int i) throws RemoteException{
        Assert.assertNotNull(localPerson);
        Assert.assertEquals(localPerson.getName(), "name" + i);
        Assert.assertEquals(localPerson.getSurname(), "surname" + i);
        Assert.assertEquals(localPerson.getPassportId(), Integer.toString(i));
        Assert.assertEquals(0, localPerson.getAccountsId().size());
    }

    @Test
    public void createGetAccountTest() throws RemoteException{
        final Person person = createPerson("Evgenii", "Shaburov", "1234");
        for (int i = 0; i < 10; i++) {
            createAccount(Integer.toString(i));
            final Person remotePerson = bank.getRemotePersonByPassport("1234");
            Assert.assertEquals(remotePerson.getAccountById(Integer.toString(i)), bank.getAccount(Integer.toString(i), person));
            final Person localPerson = bank.getLocalPersonByPassport("1234");
            Assert.assertNotNull(localPerson.getAccountById(Integer.toString(i)));
        }
    }

    @Test
    public void setAmountAccount() throws RemoteException {
        final Account account = createAccount("10");
        account.setAmount(30);
        Assert.assertEquals(30, account.getAmount());
    }

    @Test
    public void checkAmountInRemote() throws RemoteException {
        final Account account = createAccount("10");
        account.setAmount(30);
        Assert.assertEquals(30, account.getAmount());
        Assert.assertEquals(30, bank.getAccount("10", bank.getRemotePersonByPassport("1234")).getAmount());
    }

    @Test
    public void checkAmountInLocal() throws RemoteException {
        final Account account = createAccount("10");
        final Person localPerson = bank.getLocalPersonByPassport("1234");
        final Account localAccount = localPerson.getAccountById("10");
        Assert.assertEquals(0, localAccount.getAmount());
        localAccount.setAmount(30);
        Assert.assertEquals(30, localAccount.getAmount());
        Assert.assertEquals(0, account.getAmount());
    }

    @Test
    public void testMultiThreadAnotherPerson() throws InterruptedException{
        final ExecutorService bankService = Executors.newFixedThreadPool(2);
        final List<Callable<Person>> calls = new ArrayList<>();
        IntStream.range(0, 1000).forEach(i -> calls.add(() -> createPerson(Integer.toString(i),
                Integer.toString(i),
                Integer.toString(i))));
        bankService.invokeAll(calls);
    }

    Account createAccount(final String id) throws RemoteException {
        final Person person = createPerson("Evgenii", "Shaburov", "1234");
        final Account account = bank.createAccount(id, person);
        Assert.assertEquals(0, account.getAmount());
        return account;
    }

    Person createPerson(final String name, final String surname, final String passport) throws RemoteException {
        final Person person = bank.createPerson(name, surname, passport);
        Assert.assertNotNull(bank.getLocalPersonByPassport(person.getPassportId()));
        Assert.assertNotNull(bank.getRemotePersonByPassport(person.getPassportId()));
        return person;
    }
}
