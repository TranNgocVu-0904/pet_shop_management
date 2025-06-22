package DAO;

import dao.pet.PetDAO;
import model.pet.Cat;
import model.pet.Pet;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class PetDAOTest {

    @Test
    public void testAddAndFetchPet() throws Exception {
        PetDAO dao = new PetDAO();
        
        Pet cat = new Cat("Milo", "Siamese", 2, new BigDecimal("120.00"));
        Pet savedPet = dao.savePet(cat, "CAT");
        assertNotNull("Saved pet should not be null", savedPet);


        List<Pet> pets = dao.getByCondition("CAT", null);
        assertTrue(pets.stream().anyMatch(p -> p.getName().equals("Milo")));
    }
}
