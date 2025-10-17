# README.md

# Spring Boot Kafka Connect and Commercetools Integration

This project integrates a Spring Boot application with Kafka Connect to automatically push products from a PIM (MySQL) to Commercetools using a Kafka Source Connector and a Spring Boot Consumer.

## Project Structure

- `src/main/java/com/example/demo/SpringbootKafkaApplication.java`: Entry point of the Spring Boot application.
- `src/main/java/com/example/demo/config/`: Contains configuration files for Commercetools and Kafka Connect.
- `src/main/java/com/example/demo/controller/`: REST controllers for handling order and product requests.
- `src/main/java/com/example/demo/dto/`: Data transfer objects for order and product requests.
- `src/main/java/com/example/demo/kafka/`: Contains Kafka consumer and producer logic.
- `src/main/java/com/example/demo/connect/`: Kafka Connect source connector and task for MySQL PIM.
- `src/main/resources/`: Application configuration files.
- `src/test/java/com/example/demo/`: Unit tests for the application.
- `pom.xml`: Maven configuration file.

## Getting Started

1. Clone the repository.
2. Configure your MySQL database and Commercetools credentials in `application.yml`.
3. Build the project using Maven.
4. Run the application.

## License

This project is licensed under the MIT License.