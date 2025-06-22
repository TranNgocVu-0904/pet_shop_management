package Controller;

import model.product.Food;
import model.product.Toy;
import controller.bill.BillingController;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;



import static org.junit.Assert.*;

public class BillingControllerTest {

    @Test
    public void testCartAndTotalCalculation() {
        BillingController billing = new BillingController();

        Food food = new Food("Dry Food", new BigDecimal("25.00"), 10,
                LocalDate.now().plusDays(200), "Protein");
        Toy toy = new Toy("Chew Toy", new BigDecimal("10.00"), 5, "Rubber");
        
        food.setId(1);
        toy.setId(2);

        billing.addProductToCart(food, 2); // 50
        billing.addProductToCart(toy, 1);  // 10

        BigDecimal total = billing.getCartTotal();
        assertEquals(new BigDecimal("60.00"), total);
    }
}
