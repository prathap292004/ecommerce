import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiServer {

    private static final int PORT = 8090;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        ProductDAO productDAO = new ProductDAO();
        OrderDAO orderDAO = new OrderDAO();

        server.createContext("/", exchange -> {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }
            Path htmlPath = resolveFrontendHtml();
            if (htmlPath != null && Files.exists(htmlPath)) {
                byte[] bytes = Files.readAllBytes(htmlPath);
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } else {
                sendJson(exchange, 404, "{\"error\":\"index.html not found\"}");
            }
        });

        server.createContext("/api/products", exchange -> {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(method)) {
                    List<Product> list = productDAO.getAllProducts();
                    sendJson(exchange, 200, productsToJson(list));
                } else if ("POST".equalsIgnoreCase(method)) {
                    String body = readBody(exchange.getRequestBody());
                    String name = extractString(body, "name", "New Product");
                    String category = extractString(body, "category", "General");
                    BigDecimal price = new BigDecimal(extractNumber(body, "price", "0.00"));
                    int stockQty = Integer.parseInt(extractNumber(body, "stockQty", "0"));
                    String description = extractString(body, "description", "");

                    Product p = new Product(0, name, category, price, stockQty, description);
                    boolean created = productDAO.addProduct(p);
                    if (created) {
                        sendJson(exchange, 201, "{\"ok\":true,\"message\":\"Product added\"}");
                    } else {
                        sendJson(exchange, 400, "{\"ok\":false,\"message\":\"Failed to add product\"}");
                    }
                } else {
                    sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }
            } catch (SQLException | NumberFormatException e) {
                sendJson(exchange, 500, "{\"ok\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        });

        server.createContext("/api/orders", exchange -> {
            sendCors(exchange);
            String method = exchange.getRequestMethod();
            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(204, -1);
                exchange.close();
                return;
            }

            if (!"POST".equalsIgnoreCase(method)) {
                sendJson(exchange, 405, "{\"error\":\"Method not allowed\"}");
                return;
            }

            try {
                String body = readBody(exchange.getRequestBody());
                int userId = Integer.parseInt(extractNumber(body, "userId", "1"));
                int productId = Integer.parseInt(extractNumber(body, "productId", "0"));
                int qty = Integer.parseInt(extractNumber(body, "qty", "1"));

                boolean ok = orderDAO.placeOrder(userId, productId, qty);
                if (ok) {
                    sendJson(exchange, 200, "{\"ok\":true,\"message\":\"Order placed\"}");
                } else {
                    sendJson(exchange, 400, "{\"ok\":false,\"message\":\"Order failed (check stock or product)\"}");
                }
            } catch (IllegalArgumentException | SQLException e) {
                sendJson(exchange, 400, "{\"ok\":false,\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        });

        server.setExecutor(null);
        server.start();
        System.out.println("ShopCore API Server running at http://localhost:" + PORT);
    }

    private static Path resolveFrontendHtml() {
        Path p1 = Paths.get("database", "frontend", "index.html");
        if (Files.exists(p1)) return p1;
        Path p2 = Paths.get("shopcore", "database", "frontend", "index.html");
        if (Files.exists(p2)) return p2;
        return null;
    }

    private static String readBody(InputStream is) throws IOException {
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void sendCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String productsToJson(List<Product> products) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"id\":").append(p.getProductId()).append(",")
              .append("\"name\":\"").append(escapeJson(p.getName())).append("\",")
              .append("\"category\":\"").append(escapeJson(p.getCategory())).append("\",")
              .append("\"price\":").append(p.getPrice()).append(",")
              .append("\"stock\":").append(p.getStockQty()).append(",")
              .append("\"description\":\"").append(escapeJson(p.getDescription())).append("\"")
              .append("}");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String extractString(String json, String key, String fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : fallback;
    }

    private static String extractNumber(String json, String key, String fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+(?:\\.\\d+)?)").matcher(json);
        return m.find() ? m.group(1) : fallback;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}