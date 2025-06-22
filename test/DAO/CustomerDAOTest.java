package DAO;

import model.user.Customer;
import dao.customer.CustomerDAO;
import org.junit.Test;

import static org.junit.Assert.*;

public class CustomerDAOTest {

    @Test
    public void testSaveAndRetrieveCustomer() throws Exception {
        Customer customer = new Customer("Bob", "bob@mail.com", "0123456789");
        Customer saved = new CustomerDAO().saveCustomer(customer);

        assertTrue(saved.getId() > 0);

        Customer fetched = new CustomerDAO().getCustomerById(saved.getId());
        assertEquals("Bob", fetched.getName());
        assertEquals("bob@mail.com", fetched.getEmail());
    }
}
