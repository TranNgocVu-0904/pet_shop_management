package controller.product;

import dao.product.ProductDAO;
import model.product.Product;

import java.sql.SQLException;
import java.util.List;

public class ProductController {
    private static final ProductDAO productDao = new ProductDAO();

    public static boolean addProduct(Product product) {
        try {
            return productDao.saveProduct(product) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Product> getAllProducts() {
        try {
            return productDao.getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<Product> getProductsByFilter(String category, String priceOrder) {
        try {
            return productDao.getByCondition(category, priceOrder);
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static boolean updateProduct(Product product) {
        try {
            return productDao.updateProduct(product);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(int productId) {
        try {
            return productDao.deleteProduct(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
