# E-Commerce REST API

!\[Store page](docs/store.png)

Spring Boot 4 + Spring Data JPA backend for a simple online store: products, customers and orders (with stock handling).

## Requirements

* JDK 21
* Nothing else. It uses an in-memory H2 database by default (data resets on restart).

## Run

```powershell
.\\mvnw.cmd spring-boot:run          # Windows PowerShell
./mvnw spring-boot:run              # Mac/Linux
```

The store page opens at http://localhost:8080/ (product cards with pictures and a "Buy 1" button). The JSON API is at http://localhost:8080/api/products. Four sample products and one customer are created automatically.

### Use MySQL instead

Start MySQL, then:

```powershell
$env:DB\_PASSWORD="your\_mysql\_password"
.\\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Settings are in `src/main/resources/application-mysql.properties`.

## Product images

Each product has an `imageUrl`. The sample products use pictures in `src/main/resources/static/images/` (served as `/images/...`). To add your own, copy an image file into that folder and set `"imageUrl":"/images/yourfile.jpg"` when creating or updating a product. Any full `https://...` image link also works. Products without an image show `placeholder.svg`.

## Endpoints

|Method|URL|Description|
|-|-|-|
|GET|/api/products?category=Fashion|List products (category filter optional)|
|GET|/api/products/{id}|Get one product|
|POST|/api/products|Create product|
|PUT|/api/products/{id}|Update product|
|DELETE|/api/products/{id}|Delete product|
|GET|/api/customers|List customers|
|GET|/api/customers/{id}|Get one customer|
|POST|/api/customers|Create customer|
|POST|/api/orders|Place an order (reduces stock)|
|GET|/api/orders?customerId=1|List orders (optionally for one customer)|
|GET|/api/orders/{id}|Get one order|
|POST|/api/orders/{id}/cancel|Cancel order (restores stock)|

## Try it (PowerShell)

```powershell
# list products
Invoke-RestMethod http://localhost:8080/api/products

# create a product
Invoke-RestMethod -Method Post http://localhost:8080/api/products -ContentType "application/json" `
  -Body '{"name":"Laptop Bag","description":"15 inch","price":1499.00,"stock":20,"category":"Fashion","imageUrl":"/images/placeholder.svg"}'

# create a customer
Invoke-RestMethod -Method Post http://localhost:8080/api/customers -ContentType "application/json" `
  -Body '{"name":"Asha","email":"asha@example.com"}'

# place an order (customer 1 buys 2 of product 1 and 1 of product 3)
Invoke-RestMethod -Method Post http://localhost:8080/api/orders -ContentType "application/json" `
  -Body '{"customerId":1,"items":\[{"productId":1,"quantity":2},{"productId":3,"quantity":1}]}'
```

## Test

```powershell
.\\mvnw.cmd test
```

## Structure

```
com.ecommerce
├── controller   REST endpoints
├── service      business logic (stock check, totals, cancel)
├── repository   Spring Data JPA repositories
├── model        JPA entities
├── dto          request/response objects with validation
├── exception    error handling (404, 400, 409)
└── config       sample data loader
```

