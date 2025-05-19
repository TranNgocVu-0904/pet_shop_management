package Controller;

import model.Cat;
import model.Pet;
import org.junit.Before;
import org.junit.Test;
import controller.PetController;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class PetControllerTest {

//    private PetController petController;
//
//    @Before
//    public void setup() {
//        petController = new PetController();
//    }
    
    PetController petController = new PetController();

    @Test
    public void testAddAndGetPet() {
        Pet pet = new Cat("Luna", "British Shorthair", 1, new BigDecimal("200.00"));
        boolean added = petController.addPet(pet, "CAT");
        assertTrue("Pet should be added successfully", added);

        List<Pet> pets = petController.getPetsByFilter("CAT","ASC");
        assertNotNull(pets);
        assertTrue(pets.stream().anyMatch(p -> p.getName().equals("Luna")));
    }

    @Test
    public void testDeletePet() {
        Pet pet = new Cat("DeleteMe", "Random", 1, new BigDecimal("100.00"));
        petController.addPet(pet, "CAT");

        List<Pet> pets = petController.getPetsByFilter("CAT","DESC");
        Pet toDelete = pets.stream().filter(p -> p.getName().equals("DeleteMe")).findFirst().orElse(null);

        assertNotNull(toDelete);
        boolean deleted = petController.deletePet(toDelete.getId());
        assertTrue("Pet should be deleted successfully", deleted);
    }
}
