package Controller;

import controller.CustomerController;
import model.Customer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CustomerControllerTest {

    @Test
    public void testLoyaltyPointSorting() throws Exception {
        Customer c1 = new Customer("Joe01", "joe@mail.com", "0000000000");
        Customer c2 = new Customer("Max01", "max@mail.com", "1111111111");
        c1.addLoyaltyPoints(50);
        c2.addLoyaltyPoints(100);

        CustomerController.addCustomer(c1);
        CustomerController.addCustomer(c2);

        List<Customer> sorted = CustomerController.getCustomersByLoyalty("DESC");
        assertEquals("Max01", sorted.get(0).getName());
    }
}
