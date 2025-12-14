# Studently

A comprehensive student productivity platform built with Spring Boot and Java 21. Features include course management, to-do lists, time tracking with timers, calendar integration, and a terminal UI. Implements clean architecture principles and modern design patterns.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Technology Stack](#technology-stack)
- [Features](#features)
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

- **Framework**: Spring Boot 3.4.0
- **Language**: Java 21
- **Build Tool**: Maven
- **Database**: PostgreSQL (Production), H2 (Testing)
- **ORM**: JPA with Hibernate
- **Terminal UI**: Lanterna 3.1.1
- **Additional Libraries**: Lombok, Spring DevTools, Spring Actuator, Spring Validation

## Features

### User Management & Authentication
- User registration and login
- Secure password handling
- Email and username validation
- User profile management (CRUD operations)

### Course Management
- Create and manage courses with unique course codes
- Add tasks/assignments to courses with deadlines
- Track progress on course tasks (0-100%)
- Mark tasks as complete
- Update and delete courses and tasks
- View all courses for a user

### To-Do List Management
- Create multiple to-do lists per user
- Add tasks with optional deadlines
- Mark tasks as complete/incomplete
- Update and delete tasks
- Aggregate deadlines across all tasks

### Time Tracking (Timer)
- Start and stop timers for course tasks
- Track multiple timer sessions per task
- View timer history for each task
- Get total accumulated time for tasks
- Generate timer summaries (sessions, total time, active timers)

### Calendar Integration
- Unified calendar view of all tasks
- Aggregates tasks from courses and to-do lists
- Filter by month, year, and timezone
- User-specific calendar items

### Terminal UI
- Interactive terminal-based user interface
- Built with Lanterna library
- Rich text UI for command-line interaction

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
psql -U postgres -d studently -f src/main/java/com/cpp/project/course/sql/course.sql
psql -U postgres -d studently -f src/main/java/com/cpp/project/todolist/sql/todolist.sql
psql -U postgres -d studently -f src/main/java/com/cpp/project/timer/sql/timer.sql
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

### Course Management

- **POST** `/api/courses` - Create a new course
- **GET** `/api/courses/{id}` - Get course by ID
- **GET** `/api/courses/code/{code}` - Get course by code
- **GET** `/api/courses/user/{userId}` - Get all courses for a user
- **PUT** `/api/courses/{id}` - Update course
- **DELETE** `/api/courses/{id}` - Delete course

#### Course Tasks

- **POST** `/api/courses/{courseId}/tasks` - Add task to course
- **PUT** `/api/courses/{courseId}/tasks/{taskId}` - Update task
- **PUT** `/api/courses/{courseId}/tasks/{taskId}/progress` - Update task progress
- **PUT** `/api/courses/{courseId}/tasks/{taskId}/complete` - Mark task as complete
- **DELETE** `/api/courses/{courseId}/tasks/{taskId}` - Delete task

### To-Do List Management

- **POST** `/api/todolists` - Create a new to-do list
- **GET** `/api/todolists/{id}` - Get to-do list by ID
- **GET** `/api/todolists/user/{userId}` - Get all to-do lists for a user
- **PUT** `/api/todolists/{id}` - Update to-do list
- **DELETE** `/api/todolists/{id}` - Delete to-do list
- **GET** `/api/todolists/{id}/deadlines` - Get aggregated deadlines

#### To-Do List Tasks

- **POST** `/api/todolists/{todoListId}/tasks` - Add task to list
- **PUT** `/api/todolists/{todoListId}/tasks/{taskId}` - Update task
- **PUT** `/api/todolists/{todoListId}/tasks/{taskId}/complete` - Mark task as complete
- **PUT** `/api/todolists/{todoListId}/tasks/{taskId}/incomplete` - Mark task as incomplete
- **DELETE** `/api/todolists/{todoListId}/tasks/{taskId}` - Delete task

### Timer Management

- **POST** `/api/timers/start` - Start a new timer
- **POST** `/api/timers/stop` - Stop a timer
- **GET** `/api/timers/{timerId}` - Get timer by ID
- **GET** `/api/timers/task/{taskId}` - Get all timers for a task
- **GET** `/api/timers/task/{taskId}/summary` - Get timer summary for task
- **GET** `/api/timers/task/{taskId}/total` - Get total time for task

### Calendar

- **GET** `/api/calendar/items?year={year}&month={month}&userId={userId}&timezone={timezone}` - Get calendar items for month
- **GET** `/api/calendar/user/{userId}/items?year={year}&month={month}&timezone={timezone}` - Get calendar items by user

### Health & Monitoring

- **GET** `/actuator/health` - Application health status
- Additional actuator endpoints available based on configuration

## Project Structure

The application follows a feature-based package structure:

```
com.cpp.project/
├── user/                    # User domain
│   ├── entity/              # User entities
│   ├── repository/          # User data access
│   ├── service/             # User business logic
│   └── sql/                 # User schema
├── user_credential/         # User credentials domain
│   ├── entity/              # Credential entities
│   ├── repository/          # Credential data access
│   ├── service/             # Credential business logic
│   └── sql/                 # Credential schema
├── authentication/          # Authentication orchestration
│   ├── dto/                 # Authentication DTOs
│   └── service/             # Authentication services
├── course/                  # Course management domain
│   ├── entity/              # Course and task entities
│   ├── repository/          # Course data access
│   ├── service/             # Course business logic
│   ├── dto/                 # Course DTOs
│   └── sql/                 # Course schema
├── todolist/                # To-do list domain
│   ├── entity/              # To-do list entities
│   ├── repository/          # To-do list data access
│   ├── service/             # To-do list business logic
│   ├── dto/                 # To-do list DTOs
│   └── sql/                 # To-do list schema
├── timer/                   # Time tracking domain
│   ├── entity/              # Timer entities
│   ├── repository/          # Timer data access
│   ├── service/             # Timer business logic
│   ├── dto/                 # Timer DTOs
│   └── sql/                 # Timer schema
├── calendar/                # Calendar integration
│   ├── entity/              # Calendar entities
│   ├── service/             # Calendar aggregation logic
│   ├── dto/                 # Calendar DTOs
│   └── validation/          # Calendar validation
├── dashboard/               # Dashboard features
├── ui/                      # Terminal UI components
│   ├── core/                # Core UI components
│   └── handler/             # Input handlers
└── common/                  # Shared components
    ├── controller/          # REST controllers
    │   ├── dto/             # Common DTOs
    │   └── service/         # Controller implementations
    ├── sanitization/        # Input data sanitization
    │   ├── adapter/         # Sanitization adapters
    │   ├── entity/          # Sanitization entities
    │   ├── service/         # Sanitization services
    │   └── strategy/        # Sanitization strategies
    ├── validation/          # Validation framework
    │   ├── entity/          # Validation entities
    │   ├── rule/            # Validation rules
    │   └── service/         # Validators
    ├── exception/           # Exception handling
    │   ├── dto/             # Error DTOs
    │   ├── entity/          # Exception entities
    │   └── service/         # Exception handlers
    └── config/              # Application configuration
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