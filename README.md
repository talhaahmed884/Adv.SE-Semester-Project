# Studently

A Spring Boot application for user management and authentication built with Java 21, implementing clean architecture
principles and modern design patterns.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
    - [Database Setup](#database-setup)
    - [Application Configuration](#application-configuration)
    - [Building the Project](#building-the-project)
    - [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Configuration](#configuration)

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- Git (for cloning the repository)

## Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: JPA with Hibernate
- **Additional Libraries**: Lombok, Spring DevTools, Spring Actuator

## Getting Started

### Database Setup

1. **Install PostgreSQL** if not already installed

2. **Create the database**:

```bash
psql -U postgres
CREATE DATABASE studently;
\q
```

3. **Set up database credentials**:
    - Default configuration expects:
        - Host: `localhost:5432`
        - Database: `studently`
        - Username: `postgres`
        - Password: `postgres`

4. **Initialize the database schema**:

```bash
psql -U postgres -d studently -f src/main/java/com/cpp/project/user/sql/user.sql
psql -U postgres -d studently -f src/main/java/com/cpp/project/user_credential/sql/user_credential.sql
```

### Application Configuration

The application uses different profiles for different environments:

- **Default Profile**: `src/main/resources/application.properties`
- **Test Profile**: `src/test/resources/application.properties`

Update database credentials in `application.properties` if your PostgreSQL setup differs:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/studently
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Building the Project

Clone the repository and build the project:

```bash
git clone <repository-url>
cd Adv.SE-Semester-Project
mvn clean install
```

This command will:

- Download all dependencies
- Compile the source code
- Run all tests
- Package the application

### Running the Application

Start the application using Maven:

```bash
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

You can verify the application is running by checking the actuator health endpoint:

```bash
curl http://localhost:8080/actuator/health
```

## Testing

The project includes comprehensive integration tests organized by use cases.

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=UC_1_01_SignUp_Success_Basic_Test
```

### Run Test Suite

```bash
mvn test -Dtest=UserManagementTestSuite
```

### Run Tests in a Package

```bash
mvn test -Dtest="com.cpp.project.uc_1_signup.*"
```

### Test Organization

Tests follow a use case naming convention:

- Format: `UC_{number}_{UseCase}_{Scenario}_Test`
- Example: `UC_1_01_SignUp_Success_Basic_Test`

All tests extend `BaseIntegrationTest` which provides Spring Boot test configuration with the test profile.

## API Endpoints

### Authentication

- **POST** `/api/auth/signup` - Register a new user
- **POST** `/api/auth/login` - Authenticate a user

### User Management

- **GET** `/api/users/{id}` - Get user by ID
- **PUT** `/api/users/{id}` - Update user information
- **DELETE** `/api/users/{id}` - Delete user

### Health & Monitoring

- **GET** `/actuator/health` - Application health status
- Additional actuator endpoints available based on configuration

## Project Structure

The application follows a feature-based package structure:

```
com.cpp.project/
├── user/                    # User domain
├── user_credential/         # User credentials domain
├── authentication/          # Authentication orchestration
└── common/                  # Shared components
    ├── controller/          # REST controllers
    ├── sanitization/        # Input data sanitization
    ├── validation/          # Validation framework
    └── exception/           # Exception handling
```

### Key Architectural Principles

- **Clean Architecture**: Clear separation of concerns across layers
- **Design Patterns**: Extensive use of Factory, Builder, Template Method, Strategy, and Facade patterns
- **Data Processing Pipeline**: Input → Sanitize → Validate → Process
- **Transaction Management**: Service-layer transaction boundaries
- **Exception Handling**: Three-tier exception hierarchy with global handling

## Configuration

### Application Properties

Key configuration settings:

- **Server Port**: 8080 (default)
- **Database Validation**: Schema must exist (`spring.jpa.hibernate.ddl-auto=validate`)
- **Logging**: Writes to `logs/application.log` with 10MB rotation and 30-day retention
- **DevTools**: Enabled for hot reload during development

### Profiles

- **default**: Used for local development
- **test**: Used for running integration tests

## Logging

Application logs are written to:

- Console (stdout)
- File: `logs/application.log`
    - Max size: 10MB per file
    - Retention: 30 days
    - Pattern: Date-based rotation

## Development

### Hot Reload

Spring DevTools is enabled, allowing automatic restart when code changes are detected during development.

### Code Style

- Uses Lombok to reduce boilerplate code
- Follows builder pattern for DTOs and entities
- Implements interface-based service layer

## Troubleshooting

### Database Connection Issues

If you encounter database connection errors:

1. Verify PostgreSQL is running: `pg_isready`
2. Check credentials in `application.properties`
3. Ensure database `studently` exists
4. Verify SQL scripts have been executed

### Build Failures

If build fails:

1. Ensure Java 21 is installed: `java -version`
2. Clean and rebuild: `mvn clean install -U`
3. Check for port conflicts (default: 8080)

### Test Failures

If tests fail:

1. Ensure test database is properly configured
2. Check `src/test/resources/application.properties`
3. Run individual tests to isolate issues

## License

## Contributors

- [eguitar](https://github.com/eguitar)
- [noah873](https://github.com/noah873)

## Contact

For questions, issues, or contributions related to this project:

**Email**: [talhaahmed@cpp.edu](mailto:talhaahmed@cpp.edu)

**GitHub**: [talhaahmed884](https://github.com/talhaahmed884)