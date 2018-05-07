package assignments;

import bank.dao.AccountDAO;
import bank.dao.AccountDAOJPAImpl;
import bank.domain.Account;
import bank.service.AccountMgr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.DatabaseCleaner;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class AssignmentTests {
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("bankPU");
    private EntityManager em;

    @Before
    public void start(){
        em = emf.createEntityManager();
    }

    @Test
    public void assignment1(){
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);
        //Prepare a account that will be added to the database
        assertNull(account.getId());
        em.getTransaction().commit();
        System.out.println("AccountId: " + account.getId());
        //Adds the account to the database
        assertTrue(account.getId() > 0L);


    }

    @Test
    public void assignment2(){
        AccountDAO accountDAO =  new AccountDAOJPAImpl(em);
        Account account = new Account(111L);
        em.getTransaction().begin();
        em.persist(account);
        assertNull(account.getId());
        assertEquals(1, accountDAO.count());
        em.getTransaction().rollback();
        assertEquals(0, accountDAO.count());

    }

    @Test
    public void assignment3(){
        Long expected = -100L;
        Account account = new Account(111L);
        account.setId(expected);
        em.getTransaction().begin();
        em.persist(account);

        //This will not work because the id of the account is set to -100
        //assertNotEquals(expected, account.getId());
        assertEquals(expected, account.getId());

        em.flush();

        //This will not work because the entity manager is flushed and the id is a auto increment value. Flush sets it back to the correct value
        //assertEquals(expected, account.getId();
        assertNotEquals(expected, account.getId());

        em.getTransaction().commit();
    }

    @Test
    public void assignment4(){
        Long expectedBalance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(expectedBalance);
        em.getTransaction().commit();
        assertEquals(expectedBalance, account.getBalance());
        //The balance is set to the expected balance

        Long  cid = account.getId();
        account = null;
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class,  cid);
        //The balance is the same as the expected balance because the first account is retrieved with em2.find
        assertEquals(expectedBalance, found.getBalance());
    }

    @Test
    public void assignment5(){
        Long balance = 400L;
        Account account = new Account(114L);
        em.getTransaction().begin();
        em.persist(account);
        account.setBalance(balance);
        em.getTransaction().commit();
        assertEquals(balance, account.getBalance());
        Long  cid = account.getId();

        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Account found = em2.find(Account.class,  cid);
        assertEquals(balance, found.getBalance());

        Long newBalance = 150L;
        found.setBalance(newBalance);
        em2.getTransaction().commit();

        em.refresh(account);
        assertEquals(newBalance, account.getBalance());
    }

    @After
    public void CleanDatabase(){
        try {
            new DatabaseCleaner(em).clean();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
