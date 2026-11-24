# ShopCore CLI

A Java console-based e-commerce application built using the **DAO (Data Access Object)** pattern, **JDBC**, and **PostgreSQL**.

## Tech Stack
- **Language:** Java (JDK 27)
- **Database:** PostgreSQL
- **Data Access:** JDBC (`postgresql` driver)
- **CLI Input:** `BufferedReader` + `InputStreamReader`
- **IDE / Tooling:** VS Code

## Features
- **View Products (1):** Lists products from PostgreSQL with category, price, and current stock.
- **View Users (2):** Displays registered users from the database.
- **Place Order (3):** Places orders, deducts available stock, and blocks orders when stock is insufficient.
- **Add Product (4):** Adds new catalog items (name, category, price, stock quantity, description) to PostgreSQL.
- **Exit (5):** Cleanly exits the CLI menu.

## Project Structure
- `src/` — `App.java`, `DBConnection.java`, `Product.java`, `ProductDAO.java`, `User.java`, `UserDAO.java`, `OrderDAO.java`
- `database/` — `schema.sql`, `seed.sql`
- `lib/` — PostgreSQL JDBC driver JAR
- `out/` — Compiled `.class` files

## Compile & Run (Windows PowerShell)
```powershell
javac -cp "lib/*" -d out src/*.java
java -cp "out;lib/*" App