# Spring Boot + Apache Kafka + CommerceTools Integration

This project demonstrates how to integrate Spring Boot, Apache Kafka, and CommerceTools to build event-driven, commerce-oriented microservices.
It connects a PostgreSQL source, publishes data changes to Kafka topics, and syncs product data with CommerceTools via its Java SDK.


## Prerequisites

1. Java 18 or later
2. Apache Kafka installed locally
3. PostgreSQL database
4. CommerceTools project credentials (projectKey, clientId, clientSecret)


## Kafka Setup Instructions

### Generate Cluster ID
```powershell
[guid]::NewGuid().ToString()
# Example output: 4f0ebb0a-13b4-4c71-a35d-dfe6da9e464f
```

### Start Kafka Server
```cmd
cd C:\kafka\bin\windows
kafka-storage.bat format -t 4f0ebb0a-13b4-4c71-a35d-dfe6da9e464f -c ..\..\config\server.properties
kafka-server-start.bat ..\..\config\server.properties
```

### Kafka Connect Setup
To use Kafka Connect, start the Kafka server first, then launch:
```cmd
connect-distributed.bat ..\..\config\connect-distributed.properties
```

### Creating Connector Plugin
```bash
curl -X POST -H "Content-Type: application/json" --data @config/postgres-source-connector.json http://localhost:8083/connectors
```

## API Usage

### Create Product Example
```bash
curl --location 'http://localhost:8081/api/products' \
--header 'Content-Type: application/json' \
--data '{
    "key": "product-123",
    "name": "Some Product",
    "slug": "some-product",
    "sku": "SKU-123",
    "productTypeId": "furniture-and-decor",
    "description": "Product description",
    "categoryIds": ["db3abf62-cdd0-4010-9a4b-42f78d55cae5"],
    "price": {
        "currencyCode": "EUR",
        "centAmount": 4200
    },
    "image": {
        "url": "http://my.custom.cdn.net/master.png",
        "label": "Master Image",
        "dimensions": {
            "w": 303,
            "h": 197
        }
    },
    "attributes": {
        "color-code": "#FFFFF0"
    }
}'
```

### Swagger UI
API documentation is available at: http://localhost:8081/swagger-ui/index.html#/product-controller/createProduct

## PostgreSQL Setup

1. Create a database named "pim"
2. Create a table named "products"
3. Sample data insertion:
```sql
INSERT INTO products (sku, name, description, price) VALUES ('TEST-001', 'Test Product 1', 'A test product description 1', 21.99);
INSERT INTO products (sku, name, description, price) VALUES ('TEST-002', 'Test Product 2', 'A test product description 2', 22.99);

```

## CommerceTools Configuration

The application uses environment variables for CommerceTools credentials:```yaml
projectKey: 'my-project-key'
clientId: 'xeiKwt-xxxxxx'
clientSecret: '3kGs27Dgd-yyyyyyyyyy'

```

## Getting Started

1. Ensure Kafka is running
2. Start the PostgreSQL database
3. Run the Spring Boot application
4. Use the API endpoints to interact with the system
