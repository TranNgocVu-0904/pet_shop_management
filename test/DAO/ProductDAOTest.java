package DAO;

import dao.ProductDAO;
import model.Food;
import model.Product;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.*;

public class ProductDAOTest {

    @Test
    public void testSaveAndGetProduct() throws Exception {
        Product food = new Food("Dog Food", new BigDecimal("49.99"), 50,
                LocalDate.of(2025, 12, 31), "Protein, Vitamins");

        Product saved = new ProductDAO().saveProduct(food);
        assertTrue(saved.getId() > 0);

        Product fetched = new ProductDAO().getById(saved.getId());
        assertEquals("Dog Food", fetched.getName());
    }

    @Test
    public void testUpdateStock() throws Exception {
        ProductDAO dao = new ProductDAO();
        
        Product food = new Food("Test Stock", new BigDecimal("20.00"), 10,
                LocalDate.now().plusDays(365), "Fiber");
        Product saved = new ProductDAO().saveProduct(food);

        boolean updated = dao.updateStock(saved.getId(), 5); // add 5
        assertTrue(updated);

        Product result = new ProductDAO().getById(saved.getId());
        assertEquals(15, result.getStockQuantity());
    }
}
