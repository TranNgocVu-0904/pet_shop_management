package Service;

import model.BillItem;
import model.BillItem.ItemType;
import org.junit.Test;
import service.BillingService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class BillingServiceTest {

    @Test
    public void testCalculateTotal_withProductsAndPet() {
        BillingService service = new BillingService();

        BillItem food = new BillItem(1, "Dog Food", new BigDecimal("20.00"), 2);
        BillItem pet = new BillItem(101, "Golden Retriever", new BigDecimal("500.00"));

        BigDecimal total = service.calculateTotal(List.of(food, pet));
        assertEquals(new BigDecimal("540.00"), total);
    }
}