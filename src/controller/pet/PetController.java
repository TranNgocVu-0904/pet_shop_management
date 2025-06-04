package controller.pet;

import dao.pet.PetDAO;
import model.pet.Pet;
import java.util.List;

public class PetController {
    private static final PetDAO petDao = new PetDAO();

    public static List<Pet> getAllPets() {
        try {
            return petDao.getAllPets();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<Pet> getPetsByFilter(String type, String priceOrder) {
        try {
            return petDao.getByCondition(type, priceOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static boolean addPet(Pet pet, String type) {
        try {
            return petDao.savePet(pet, type) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePet(Pet pet) {
        try {
            return petDao.updatePet(pet);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deletePet(int petId) {
        try {
            return petDao.deletePet(petId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
