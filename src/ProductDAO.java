import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, name, category, price, stock_qty, description FROM products ORDER BY product_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_qty"),
                        rs.getString("description")
                );
                products.add(p);
            }
        }
        return products;
    }

    public boolean addProduct(Product p) throws SQLException {
        String sql = "INSERT INTO products (name, category, price, stock_qty, description) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setBigDecimal(3, p.getPrice());
            ps.setInt(4, p.getStockQty());
            ps.setString(5, p.getDescription());

            return ps.executeUpdate() > 0;
        }
    }
}
