import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {

    public boolean placeOrder(int userId, int productId, int qty) throws SQLException {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        String selectSql = "SELECT price, stock_qty FROM products WHERE product_id = ?";
        String updateStockSql = "UPDATE products SET stock_qty = stock_qty - ? WHERE product_id = ? AND stock_qty >= ?";
        String insertOrderSql = "INSERT INTO orders (user_id, total_amount, status) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try {
                BigDecimal price;
                int currentStock;

                try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                    selectPs.setInt(1, productId);
                    try (ResultSet rs = selectPs.executeQuery()) {
                        if (!rs.next()) {
                            conn.rollback();
                            System.out.println("Product not found: #" + productId);
                            return false;
                        }
                        price = rs.getBigDecimal("price");
                        currentStock = rs.getInt("stock_qty");
                    }
                }

                if (currentStock < qty) {
                    conn.rollback();
                    System.out.println("Insufficient stock! Available: " + currentStock);
                    return false;
                }

                try (PreparedStatement updatePs = conn.prepareStatement(updateStockSql)) {
                    updatePs.setInt(1, qty);
                    updatePs.setInt(2, productId);
                    updatePs.setInt(3, qty);
                    if (updatePs.executeUpdate() == 0) {
                        conn.rollback();
                        return false;
                    }
                }

                BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(qty));
                try (PreparedStatement orderPs = conn.prepareStatement(insertOrderSql)) {
                    orderPs.setInt(1, userId);
                    orderPs.setBigDecimal(2, totalAmount);
                    orderPs.setString(3, "CONFIRMED");
                    orderPs.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}