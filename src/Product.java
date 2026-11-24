import java.math.BigDecimal;

public class Product {
    private int productId;
    private String name;
    private String category;
    private BigDecimal price;
    private int stockQty;
    private String description;

    public Product() {
    }

    public Product(int productId, String name, String category, BigDecimal price, int stockQty, String description) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQty = stockQty;
        this.description = description;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("#%d | %s (%s) | Rs. %s | Stock: %d",
                productId, name, category, price, stockQty);
    }
}