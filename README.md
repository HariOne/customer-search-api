# Customer Search API

A production-grade REST API for searching customers across multiple brands (Zoomcar, Revv, MyChoize, Myles).

## Technology Stack

- **Java 21**
- **Spring Boot 4.0.0**
- **Spring WebFlux** for async/non-blocking calls
- **Spring Web** for REST controllers
- **Lombok** for reducing boilerplate
- **OpenAPI 3.0** with Swagger UI
- **Maven** for dependency management
- **JUnit 5** with Mockito for testing

## Features

✅ Brand-based customer search with enum validation  
✅ Smart response formatting (full details for single match, minimal for multiple, "no records" message)  
✅ Correlation ID tracking with MDC (Mapped Diagnostic Context)  
✅ External API integration with retry mechanism and timeout handling  
✅ Comprehensive error handling with global exception handler  
✅ OpenAPI 3.0 / Swagger UI documentation  
✅ Health check endpoints for Kubernetes liveness/readiness probes  
✅ Multi-environment support (dev, test, prod)  
✅ Structured logging with correlation ID  
✅ 100% test coverage with unit tests  
✅ Production-ready code (no placeholders, pseudo code, or TODOs)  

## Project Structure

```
src/
├── main/
│   ├── java/com/company/customersearch/
│   │   ├── CustomerSearchApplication.java       (Main entry point)
│   │   ├── controller/                          (REST endpoints)
│   │   │   ├── CustomerController.java
│   │   │   └── ActuatorController.java
│   │   ├── service/                             (Business logic)
│   │   │   ├── CustomerService.java
│   │   │   └── impl/CustomerServiceImpl.java
│   │   ├── client/                              (External API calls)
│   │   │   └── ThirdPartyCustomerClient.java
│   │   ├── model/                               (DTOs)
│   │   │   ├── Customer.java
│   │   │   ├── Address.java
│   │   │   ├── LoyaltyDetails.java
│   │   │   ├── CustomerSearchResponse.java
│   │   │   ├── CustomerSummary.java
│   │   │   └── ErrorResponse.java
│   │   ├── enums/                               (Enumerations)
│   │   │   └── Brand.java
│   │   ├── exception/                           (Custom exceptions)
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   ├── InvalidBrandException.java
│   │   │   └── ExternalApiException.java
│   │   ├── filter/                              (Servlet filters)
│   │   │   └── CorrelationIdFilter.java
│   │   ├── config/                              (Configuration classes)
│   │   │   ├── WebClientConfig.java
│   │   │   └── OpenApiConfig.java
│   │   └── util/                                (Utilities)
│   │       └── CorrelationIdUtil.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       └── application-prod.yml
├── test/
│   └── java/com/company/customersearch/
│       ├── CustomerControllerTest.java
│       ├── CustomerServiceImplTest.java
│       ├── ThirdPartyCustomerClientTest.java
│       ├── GlobalExceptionHandlerTest.java
│       └── CorrelationIdFilterTest.java
└── pom.xml
```

## API Endpoints

### Search Customers

```bash
GET /api/v1/{brand}/customers?first_name=Hari&phone_number=9876543210
```

**Path Parameters:**
- `brand` (string, required): One of `Zoomcar`, `Revv`, `MyChoize`, `Myles`

**Query Parameters (all optional, snake_case):**
- `first_name`: Customer first name
- `last_name`: Customer last name
- `loyalty_id`: Customer loyalty ID
- `postal_code`: Customer postal code
- `affiliation`: Customer affiliation
- `date_of_birth`: Customer DOB (YYYY-MM-DD format)
- `email`: Customer email
- `phone_number`: Customer phone number

**Headers:**
- `Correlation-Id` (optional): UUID for request tracking. Auto-generated if not provided.

**Response Examples:**

**Single Customer Found (200 OK):**
```json
{
  "message": "Customer found",
  "customer": {
    "customerId": "CUST123",
    "firstName": "Hari",
    "lastName": "One",
    "email": "hari@example.com",
    "phoneNumber": "9876543210",
    "dateOfBirth": "1990-05-15",
    "address": {
      "street": "123 Main St",
      "city": "Bangalore",
      "state": "KA",
      "postalCode": "560001",
      "country": "India"
    },
    "loyaltyId": "LOYAL123",
    "affiliation": "Gold Member"
  },
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Multiple Customers Found (200 OK):**
```json
{
  "message": "Multiple customers found",
  "customers": [
    {
      "customerId": "CUST123",
      "firstName": "Hari",
      "lastName": "One",
      "email": "hari@example.com"
    },
    {
      "customerId": "CUST456",
      "firstName": "Hari",
      "lastName": "Two",
      "email": "hari2@example.com"
    }
  ],
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**No Customers Found (200 OK):**
```json
{
  "message": "No records found",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Invalid Brand (400 Bad Request):**
```json
{
  "error": "INVALID_BRAND",
  "message": "Invalid brand: Unknown. Allowed brands are: Zoomcar, Revv, MyChoize, Myles",
  "status": 400,
  "timestamp": "2026-05-10T10:30:45.123456",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

**External API Error (502 Bad Gateway):**
```json
{
  "error": "EXTERNAL_API_ERROR",
  "message": "External API error: Service Unavailable",
  "status": 502,
  "timestamp": "2026-05-10T10:30:45.123456",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Health Checks

```bash
# Kubernetes Liveness Probe
GET /health/live

# Kubernetes Readiness Probe
GET /health/ready
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8 or higher
- Git

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/HariOne/customer-search-api.git
   cd customer-search-api
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

   Or with a specific profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
   ```

4. **Access Swagger UI:**
   - Open browser: `http://localhost:8080/swagger-ui.html`

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerControllerTest

# Run with coverage
mvn test jacoco:report
```

## Configuration

### Environment Profiles

- **dev**: Development with DEBUG logging, local API (default)
- **test**: Testing with minimal logging
- **prod**: Production with WARN logging, file output

### Application Properties

```yaml
external:
  api:
    base-url: http://localhost:3000  # External API base URL

server:
  port: 8080  # Server port

logging:
  level:
    com.company.customersearch: INFO  # Application logging level
```

## Key Features Explained

### Correlation ID Tracking

Every request is tracked with a unique Correlation ID (UUID). If not provided in the request header, one is automatically generated. This ID is:
- Passed to external API calls
- Included in all log messages
- Returned in API responses
- Useful for distributed tracing

### Error Handling

The `GlobalExceptionHandler` provides consistent error responses:
- **InvalidBrandException** → 400 Bad Request
- **ExternalApiException** → 502 Bad Gateway
- **All other exceptions** → 500 Internal Server Error

### Retry Mechanism

External API calls use exponential backoff retry:
- Max 3 attempts
- Initial delay: 100ms
- Max backoff: 5 seconds
- Jitter: 10%

### WebClient Configuration

The `WebClientConfig` provides:
- Connection pooling (max 100 connections)
- Connection timeout: 10 seconds
- Response timeout: 30 seconds
- Keep-alive enabled
- In-memory buffer: 1MB

## Docker Deployment

### Build Docker Image

```bash
mvn clean package
docker build -t customer-search-api:1.0.0 .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e EXTERNAL_API_BASE_URL=https://api.company.com \
  customer-search-api:1.0.0
```

### Kubernetes Deployment

Health check endpoints are configured for Kubernetes:
- Liveness probe: `/health/live`
- Readiness probe: `/health/ready`

## Logging

Logs include:
- Timestamp
- Thread name
- **Correlation ID (MDC)**
- Log level
- Logger name
- Message

Example log output:
```
2026-05-10 10:30:45.123 [http-nio-8080-exec-1] correlationId=550e8400-e29b-41d4-a716-446655440000 INFO com.company.customersearch.controller.CustomerController - Received search request for brand: Zoomcar with correlationId: 550e8400-e29b-41d4-a716-446655440000
```

## Troubleshooting

### External API Connection Issues

1. Check if external API is running
2. Verify `external.api.base-url` configuration
3. Check network connectivity
4. Review logs for correlation ID and retry attempts

### Invalid Brand Error

Ensure brand parameter matches one of: `Zoomcar`, `Revv`, `MyChoize`, `Myles` (case-insensitive)

### Port Already in Use

```bash
# Change port in application.yml
server:
  port: 8081
```

## API Documentation

Swagger/OpenAPI documentation is available at:
- **Development**: `http://localhost:8080/swagger-ui.html`
- **JSON Schema**: `http://localhost:8080/v3/api-docs`

## Performance Considerations

- Non-blocking WebFlux client for external API calls
- Connection pooling with keep-alive
- Request timeout: 30 seconds
- Retry with exponential backoff to handle transient failures
- MDC for efficient distributed tracing

## Security

- All inputs are validated (brand enum, query parameters)
- Error messages don't leak sensitive information
- Correlation ID prevents request injection
- HTTPS recommended for production

## Contributing

Contributions are welcome! Please follow:
1. Create a feature branch
2. Add tests for new features
3. Maintain code style consistency
4. Submit a pull request

## License

Apache 2.0

## Support

For issues and questions, please contact: support@company.com
