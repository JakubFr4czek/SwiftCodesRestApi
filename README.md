
# SWIFT Code Parser and API

## Description

This project implements an application that parses SWIFT codes, stores them in a database, and exposes a RESTful API to retrieve, add, and delete SWIFT code data. The application is built using Java, Spring Boot, and Docker. It interacts with a SWIFT code data file, parses the information, stores it in a fast-access database, and provides endpoints for interaction.

## Project Requirements

- Parse SWIFT code data from a provided file.
- Store the parsed data in a fast-access database (relational or non-relational).
- Expose a REST API with endpoints to retrieve, add, and delete SWIFT codes.

## Technologies Used

- Java 17
- Spring Boot
- Docker
- PostgreSQL
- REST API

## API Endpoints

### 1. Retrieve details of a single SWIFT code
**GET**: `/v1/swift-codes/{swift-code}`

**Response Structure (Headquarter):**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": true,
    "swiftCode": "string",
    "branches": [
        {
            "address": "string",
            "bankName": "string",
            "countryISO2": "string",
            "isHeadquarter": false,
            "swiftCode": "string"
        },
        ...
    ]
}
```

**Response Structure (Branch):**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": false,
    "swiftCode": "string"
}
```

### 2. Retrieve all SWIFT codes for a specific country
**GET**: `/v1/swift-codes/country/{countryISO2code}`

**Response Structure:**
```json
{
    "countryISO2": "string",
    "countryName": "string",
    "swiftCodes": [
        {
            "address": "string",
            "bankName": "string",
            "countryISO2": "string",
            "isHeadquarter": true,
            "swiftCode": "string"
        },
        ...
    ]
}
```

### 3. Add a new SWIFT code entry
**POST**: `/v1/swift-codes`

**Request Structure:**
```json
{
    "address": "string",
    "bankName": "string",
    "countryISO2": "string",
    "countryName": "string",
    "isHeadquarter": true,
    "swiftCode": "string"
}
```

**Response Structure:**
```json
{
    "message": "SWIFT code added successfully"
}
```

### 4. Delete a SWIFT code entry
**DELETE**: `/v1/swift-codes/{swift-code}`

**Response Structure:**
```json
{
    "message": "SWIFT code deleted successfully"
}
```

## Key Expectations

- Include all code in a GitHub repository with a well-structured and clear README.md.
- Ensure solution correctness and maintain a high standard of code quality.
- Verify that all endpoints and responses align with the structure outlined in the exercise description.
- Handle all edge cases gracefully, with clear and informative error messages.
- Provide thorough unit and integration tests to ensure reliability.
- Containerize the application and database, ensuring the endpoints are accessible at localhost:8080.

## How to Run

### Prerequisites

- Java 17 or later
- Docker

### Setup Instructions

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repository-name.git
   ```

2. Navigate to the project directory:
   ```bash
   cd swift-code-api
   ```

3. Build the project:
   ```bash
   ./mvnw clean install
   ```

4. Start the application using Docker:
   ```bash
   docker-compose up
   ```

5. The application will be accessible at `http://localhost:8080`.

### Running Tests

To run tests, you can use the following command:
```bash
./mvnw test
```

### Docker

This project is containerized using Docker. To run the application and database in containers, use:
```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`.
