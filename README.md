# Product Catalog Microservice
A microservice that manages product information, supports adding, updating, and fetching products, and publishes events (on creation/update) to RabbitMQ.

## Tech Stack:
Java 21
Spring Boot
PostgreSQL
RabbitMQ (for event-driven design)
Maven (build tool)
Docker (containerization)

## Features:
- Create, update, and retrieve products via REST APIs
- Persist product data in PostgreSQL using JPA
- Publish events to GCP Pub/Sub when products are created or updated(Done using RabbitMQ)
- Event-driven architecture with RabbitMQ for product creation and update events.
- Optionally support RabbitMQ for event publishing
- Ready for cloud deployment (Docker + Cloud Run compatible)

# Prerequisites
Before starting, make sure to have the following tools installed on the machine:
- Docker (for running the app and dependencies)
- Docker Compose (for multi-container orchestration)
- Maven (for building the application)

# Setup Instructions
1.Setup instructions (local DB, how to run)
Prerequisites
- Docker
- Docker Compose

Step i) create docker-compose.yml

```
version: '3.8'

  postgres:
    image: postgres:14
    container_name: postgres_container
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
      POSTGRES_DB: productdb
    ports:
      - "5436:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network

  pgadmin:
    image: dpage/pgadmin4
    container_name: pgadmin_container
    restart: always
    environment:
      PGADMIN_DEFAULT_EMAIL: admin@admin.com
      PGADMIN_DEFAULT_PASSWORD: admin
    ports:
      - "5050:80"
    depends_on:
      - postgres
    networks:
      - app-network

```

Step ii) Start the containers

```
docker-compose up -d
```

- PostgreSQL will be accessible at localhost:5432
- pgAdmin will be accessible at ```http://localhost:5050/```

Step iii) Login to pgAdmin
Open browser: http://localhost:5050
Use the following credentials:
- Email: admin@admin.com
- Password: admin

Click Add New Server:
- Name: postgres
- Host: postgres
- Port: 5432
- Username: postgres
- Password: root

Step iv) Stop and remove containers
```
docker-compose down
```

```
docker-compose down -v
```
2.RabbitMQ Setup
Stepi) Update docker-compose.yml under services

```
rabbitmq:
    image: rabbitmq:management
    container_name: rabbitmq
    ports:
      - "5672:5672"    # AMQP port
      - "15672:15672"  # RabbitMQ Management UI
    environment:
      - RABBITMQ_DEFAULT_USER=guest
      - RABBITMQ_DEFAULT_PASS=guest
    restart: unless-stopped
    networks:
      - app-network
```

Step ii) Start the containers   

```
docker-compose up -d
```

- RabbitMQ will be accessible at  ```http://localhost:15672/```

Step iii) Login to rabbitmq
Open browser: http://localhost:5050
Use the following credentials:
- Email: guest
- Password: guest

# Running the application
Step i)Create Dockerfile for the application

```
FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use official OpenJDK image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the built jar file into the container
COPY --from=builder /app/target/product-catalog-0.0.1-SNAPSHOT.jar app.jar
# Expose the port your app will run on
EXPOSE 8080

# Default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

```
Step ii)Update docker-compose.yml
```
springboot-app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: springboot-app
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq 
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/productdb
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_RABBITMQ_USERNAME: guest
      SPRING_RABBITMQ_PASSWORD: guest
```
Step iii)Running the application , pgAdmin, postgres, rabbitmq(all dockerized)

```
docker compose up -d
```
CHECK all containers are in running using

```
docker ps
```


# API Usage examples

## POST /products/added – Add a new product
RequestBody
```
{
  "name": "Galaxy S24 Ultra",
  "description": "For Power users, mobile photographers, and productivity enthusiasts",
  "category": "Electronics",
  "price": 1300,
  "availableStock": 100
}
```

ON POSTMAN
<img width="1269" alt="image" src="https://github.com/user-attachments/assets/118045c8-79ef-4458-922a-d1be3c696cb1" />



ON pgAdmin
<img width="1499" alt="image" src="https://github.com/user-attachments/assets/5bea0b9e-a76b-47fd-954e-3c737c531084" />



ON RabbitMQ
<img width="1508" alt="image" src="https://github.com/user-attachments/assets/6f9f90c6-2390-4259-a7c3-2428865f6c8f" />


ON Application console

```
docker logs <container_id_springboot_app>
```

<img width="1488" alt="image" src="https://github.com/user-attachments/assets/cc2ee6e8-e1b5-4c4d-93cb-26cda926566a" />


Published event to RabbitMQ: EventPayload(eventType=PRODUCT_CREATED, timestamp=2025-04-29T06:59:02.746992929Z, product=Product(productId=67191edb-68ed-4d7a-9159-92575608c968, name=Galaxy S24 Ultra, description=For Power users, mobile photographers, and productivity enthusiasts, category=Electronics, price=1300.0, availableStock=100, lastUpdated=2025-04-29T06:59:02.724626845Z))

## PUT /products/{id} – Update Product

RequestBody
```
{
    "name": "Galaxy S24 updated version",
    "description": "For Power users, mobile photographers, and extra productivity",
    "category": "Electronics",
    "price": 1999.0,
    "availableStock": 30
}

```
ON POSTMAN
<img width="1278" alt="image" src="https://github.com/user-attachments/assets/44bb476a-ee85-4c10-b2d4-534c29371b81" />




ON pgAdmin
<img width="1512" alt="image" src="https://github.com/user-attachments/assets/887e53b2-8932-4ce0-912f-c5523a364332" />




ON RabbitMQ
<img width="1512" alt="image" src="https://github.com/user-attachments/assets/0fc1b9ad-8e9c-4c74-9bce-c78921b722db" />



ON Application console
<img width="1481" alt="image" src="https://github.com/user-attachments/assets/6ab2ed67-086c-45dd-ba9a-d7a1bf0fe314" />

Published event to RabbitMQ: EventPayload(eventType=PRODUCT_CREATED, timestamp=2025-04-29T06:59:02.746992929Z, product=Product(productId=67191edb-68ed-4d7a-9159-92575608c968, name=Galaxy S24 Ultra, description=For Power users, mobile photographers, and productivity enthusiasts, category=Electronics, price=1300.0, availableStock=100, lastUpdated=2025-04-29T06:59:02.724626845Z))
Published event to RabbitMQ: EventPayload(eventType=PRODUCT_UPDATED, timestamp=2025-04-29T07:05:05.065393721Z, product=Product(productId=67191edb-68ed-4d7a-9159-92575608c968, name=Galaxy S24 updated version, description=For Power users, mobile photographers, and extra productivity, category=Electronics, price=1999.0, availableStock=30, lastUpdated=2025-04-29T07:05:05.059481846Z)

## GET /products/all – List All Products
<img width="1494" alt="image" src="https://github.com/user-attachments/assets/1895cf5d-8967-4b5c-87b4-d7bd2cd84fa6" />

ON Postman
<img width="1254" alt="image" src="https://github.com/user-attachments/assets/387fd6ca-72dd-431b-858e-15b46848a1ea" />


```
curl http://localhost:8080/products/<UUID>

```
NO CHANGE IN RABBITMQ as no event is published

## GET /products/{id} – Get Product by ID

USE
```
curl http://localhost:8080/products/67191edb-68ed-4d7a-9159-92575608c968
```

On Postman
<img width="1285" alt="image" src="https://github.com/user-attachments/assets/cbd9cf43-9b34-424d-a10f-ebb82d891b3c" />

NO CHANGE IN RABBITMQ as no event is published

## API Docs 
Visit: ```http://localhost:8080/swagger-ui.html``` and ```http://localhost:8080/api-docs```

