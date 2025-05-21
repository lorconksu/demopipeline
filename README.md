# Spring Boot Demo Pipeline Application

This is a containerized Spring Boot demo application designed for CI/CD pipeline demonstration. It includes a complete setup for:

- Maven build with JaCoCo for test coverage
- SonarQube integration with 85%+ test coverage
- Docker containerization
- Security scanning with Grype
- Helm chart for Kubernetes deployment
- Jenkins pipeline configuration

## Application Overview

This application is a simple RESTful API for managing products with the following endpoints:

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get a product by ID
- `GET /api/products/category/{category}` - Get products by category
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update a product
- `DELETE /api/products/{id}` - Delete a product

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker
- Kubernetes cluster (for deployment)
- SonarQube server
- Jenkins with Kubernetes plugin

## Running Locally

### Using Maven

```bash
mvn clean install
mvn spring-boot:run
```

### Using Docker

```bash
docker build -t demo-pipeline .
docker run -p 8080:8080 demo-pipeline
```

### Using Docker Compose (with SonarQube)

```bash
docker-compose up -d
```

## Testing

Run unit and integration tests:

```bash
mvn test
```

Generate test coverage report:

```bash
mvn verify
```

The JaCoCo report will be available at `target/site/jacoco/index.html`.

## SonarQube Analysis

The application is configured to achieve a code coverage of at least 85%. To run a SonarQube analysis:

```bash
mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your-token
```

## Security Scanning

The Docker image can be scanned using Grype:

```bash
grype demo-pipeline:latest --fail-on high
```

## Helm Deployment

Deploy the application to Kubernetes:

```bash
helm install demo-pipeline ./helm/demo-pipeline
```

## CI/CD Pipeline

The included Jenkinsfile defines a complete pipeline with the following stages:

1. Checkout
2. Build & Test
3. SonarQube Analysis
4. SonarQube Quality Gate
5. Build Docker Image
6. Security Scan
7. Push Image
8. Deploy to Kubernetes

## Project Structure

```
.
├── Dockerfile
├── Jenkinsfile
├── README.md
├── docker-compose.yml
├── helm/
│   └── demo-pipeline/
│       ├── Chart.yaml
│       ├── templates/
│       │   ├── _helpers.tpl
│       │   ├── deployment.yaml
│       │   └── service.yaml
│       └── values.yaml
├── pom.xml
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── example/
    │               └── demopipeline/
    │                   ├── DemoPipelineApplication.java
    │                   ├── controller/
    │                   ├── exception/
    │                   ├── model/
    │                   └── service/
    └── test/
        └── java/
            └── com/
                └── example/
                    └── demopipeline/
                        ├── DemoPipelineApplicationTests.java
                        ├── controller/
                        └── service/
```

## Notes

This application is designed to meet the following pipeline requirements:
- Containerized build stage with Maven
- SonarQube integration with 85% code coverage
- Security scan with Grype (no critical or high vulnerabilities)
- Helm chart for Kubernetes deployment.
