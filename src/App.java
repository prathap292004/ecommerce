import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        ProductDAO productDAO = new ProductDAO();
        UserDAO userDAO = new UserDAO();
        OrderDAO orderDAO = new OrderDAO();

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;

        while (running) {
            System.out.println("\n=== ShopCore CLI Menu ===");
            System.out.println("1. View Products");
            System.out.println("2. View Users");
            System.out.println("3. Place Order");
            System.out.println("4. Add Product");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String line = reader.readLine();
            if (line == null) {
                break;
            }
            String choice = line.trim();

            try {
                switch (choice) {
                    case "1":
                        List<Product> products = productDAO.getAllProducts();
                        System.out.println("\n--- Products ---");
                        if (products.isEmpty()) {
                            System.out.println("No products found.");
                        } else {
                            for (Product p : products) {
                                System.out.println(p);
                            }
                        }
                        break;

                    case "2":
                        List<User> users = userDAO.getAllUsers();
                        System.out.println("\n--- Users ---");
                        if (users.isEmpty()) {
                            System.out.println("No users found.");
                        } else {
                            for (User u : users) {
                                System.out.println(u);
                            }
                        }
                        break;

                    case "3":
                        System.out.print("Enter User ID: ");
                        int userId = Integer.parseInt(reader.readLine().trim());

                        System.out.print("Enter Product ID: ");
                        int productId = Integer.parseInt(reader.readLine().trim());

                        System.out.print("Enter Quantity: ");
                        int qty = Integer.parseInt(reader.readLine().trim());

                        boolean ordered = orderDAO.placeOrder(userId, productId, qty);
                        System.out.println(ordered ? "Order placed successfully!" : "Order could not be placed.");
                        break;

                    case "4":
                        System.out.print("Enter Product Name: ");
                        String name = reader.readLine().trim();

                        System.out.print("Enter Category: ");
                        String category = reader.readLine().trim();

                        System.out.print("Enter Price: ");
                        BigDecimal price = new BigDecimal(reader.readLine().trim());

                        System.out.print("Enter Stock Quantity: ");
                        int stockQty = Integer.parseInt(reader.readLine().trim());

                        System.out.print("Enter Description: ");
                        String description = reader.readLine().trim();

                        Product newProduct = new Product(0, name, category, price, stockQty, description);
                        boolean added = productDAO.addProduct(newProduct);
                        System.out.println(added ? "Product added to catalog!" : "Failed to add product.");
                        break;

                    case "5":
                        running = false;
                        System.out.println("Exiting ShopCore. Bye!");
                        break;

                    default:
                        System.out.println("Invalid option. Try 1 to 5.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (SQLException e) {
                System.out.println("DB Error: " + e.getMessage());
            }
        }
    }
}