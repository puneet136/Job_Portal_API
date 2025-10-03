# Job Portal Application

## Overview

The **Job Portal Application** is a Spring Boot-based web application designed to facilitate job postings, applications, and user management. Built with Java 21 and leveraging modern frameworks like Spring Security, Spring Data JPA, and Springdoc OpenAPI, this project provides a RESTful API for managing job listings, user authentication, and application tracking. It supports multiple user roles (USER, EMPLOYER, ADMIN) with JWT-based authentication to ensure secure access to resources.

This application is ideal for learning Spring Boot, implementing role-based access control, and integrating Swagger for API documentation. It includes a MySQL database for persistent storage and offers endpoints for registering users, posting jobs, applying to jobs, and managing user profiles.

---

## Features

- **User Authentication**: Register and log in with email, username, and password, secured with JWT tokens.
- **Role-Based Access**: Supports three roles—USER (job seekers), EMPLOYER (job posters), and ADMIN (administrators) with specific permissions.
- **Job Management**: Create, read, update, and delete job postings (restricted to EMPLOYERs).
- **Job Applications**: Allow USERs to apply for jobs with resume URLs and track application status.
- **User Management**: ADMINs can view, update, or delete user accounts; all users can update their own profiles.
- **API Documentation**: Integrated Swagger UI for interactive API testing and documentation.
- **Pagination**: Supports paginated responses for job listings and applications.

---

## Technologies Used

- **Java**: 21 (latest LTS version for robust performance).
- **Spring Boot**: 3.5.6 (core framework for REST APIs and dependency management).
- **Spring Security**: 6.5.5 (for authentication and authorization).
- **Spring Data JPA**: For database operations with MySQL.
- **MySQL**: 8.x (relational database for storing users, jobs, and applications).
- **JWT**: JSON Web Tokens (via JJWT library) for secure authentication.
- **Springdoc OpenAPI**: 2.6.0 (for Swagger UI and API documentation).
- **Lombok**: Simplifies boilerplate code (e.g., getters, setters).
- **Hibernate Validator**: For input validation.
- **Maven**: Build and dependency management.

---

## Project Structure

```
Job_Portal/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.api.Job_Portal/
│   │   │       ├── config/           # Security, JWT, and Swagger configurations
│   │   │       ├── controller/       # REST controllers for APIs
│   │   │       ├── entity/           # JPA entities (e.g., User, JobPost, Application)
│   │   │       ├── repository/       # JPA repositories
│   │   │       ├── service/          # Business logic and services
│   │   │       └── JobPortalApplication.java  # Main application class
│   │   └── resources/
│   │       ├── application.properties  # Configuration (e.g., database, server)
│   └── test/                         # Unit and integration tests
├── pom.xml                           # Maven build file
└── README.md                         # This file
```

---

## Prerequisites

- **Java Development Kit (JDK)**: 21 or higher.
- **Maven**: 3.6+ for building the project.
- **MySQL**: 8.x with a running instance.
- **IDE**: IntelliJ IDEA, Eclipse, or any Java-compatible IDE (optional but recommended).
- **Postman or cURL**: For testing APIs (optional).

---

## Installation and Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/Job_Portal.git
cd Job_Portal
```

### 2. Configure the Database
- Install MySQL and create a database named `job_portal`.
- Update `src/main/resources/application.properties` with your MySQL credentials:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/job_portal?useSSL=false&serverTimezone=UTC
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  spring.jpa.hibernate.ddl-auto=update
  spring.jpa.show-sql=true
  spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
  ```

### 3. Build the Project
- Run the following command to build and resolve dependencies:
  ```bash
  mvn clean install
  ```
- This will download all required libraries and compile the code.

### 4. Run the Application
- Start the application using Maven:
  ```bash
  mvn spring-boot:run
  ```
- Alternatively, run `JobPortalApplication.java` directly from your IDE.
- The application will start on `http://localhost:8080`.

### 5. Verify Setup
- Open a browser or use `curl` to check the health endpoint (if implemented) or access Swagger UI at `http://localhost:8080/swagger-ui.html`.

---

## Usage

### API Endpoints
All endpoints are secured with JWT authentication unless marked as public. Use the token from the `/api/auth/login` response in the `Authorization` header as `Bearer <token>`.

#### Authentication
- **Register**: `POST /api/auth/register`
  - Payload: `{"email": "user@example.com", "username": "username", "password": "password123" , "role" : "USER/ADMIN/EMPLOYER"}`
  - Role: Public
- **Login**: `POST /api/auth/login`
  - Payload: `{"email": "user@example.com", "password": "password123"}`
  - Role: Public
  - Response: Returns a JWT token.

#### Jobs
- **Get All Jobs**: `GET /api/jobs?page=0&size=10`
  - Role: Public
- **Get Job by ID**: `GET /api/jobs/1`
  - Role: Public
- **Create Job**: `POST /api/jobs`
  - Payload: `{"title": "Job Title", "description": "Job Description", "location": "Location", "skills": "Skill1, Skill2", "salary": 80000.0, "category": {"id": 1}}`
  - Role: EMPLOYER
- **Update Job**: `PUT /api/jobs/1`
  - Payload: `{"title": "Updated Job Title", ...}`
  - Role: EMPLOYER (owner only)
- **Delete Job**: `DELETE /api/jobs/1`
  - Role: EMPLOYER (owner only)

#### Applications
- **Apply for Job**: `POST /api/jobs/1/apply`
  - Payload: `{"resumeUrl": "https://example.com/resume.pdf", "status": "PENDING"}`
  - Role: USER
- **Get My Applications**: `GET /api/applications?page=0&size=10`
  - Role: USER
- **Get Applications by Job**: `GET /api/applications/job/1?page=0&size=10`
  - Role: EMPLOYER (owner only)

#### User Management
- **Get All Users**: `GET /api/admin/users`
  - Role: ADMIN
- **Get User by ID**: `GET /api/admin/users/1`
  - Role: ADMIN
- **Update User by ID**: `PUT /api/admin/users/1`
  - Payload: `{"email": "newemail@example.com", "username": "newuser", "password": "newpass", "role": {"id": 1}}`
  - Role: ADMIN
- **Delete User by ID**: `DELETE /api/admin/users/1`
  - Role: ADMIN
- **Get Current User**: `GET /api/users/me`
  - Role: Authenticated (any role)
- **Update Current User**: `PUT /api/users/me`
  - Payload: `{"email": "newemail@example.com", "username": "newuser", "password": "newpass"}`
  - Role: Authenticated (any role)

### Testing with Swagger
- Access `http://localhost:8080/swagger-ui.html` to explore and test all endpoints interactively.
- Authenticate by adding the JWT token in the "Authorize" section with the `bearerAuth` scheme.

---

## Configuration

### Security
- **JWT**: Configured in `JwtAuthenticationFilter` and `SecurityConfig` to secure endpoints.
- **Roles**: Defined in the database and enforced via Spring Security.
- **CORS**: Disabled by default; enable if needed by adding `.cors()` in `SecurityConfig`.

### Database
- Tables are auto-generated based on JPA entities.
- Use `spring.jpa.hibernate.ddl-auto=update` for development; set to `none` in production.

---

## Troubleshooting

### Common Issues
- **401 Unauthorized**: Ensure the JWT token is valid and included in the `Authorization` header. Regenerate via `/api/auth/login`.
- **404 Not Found**: Verify IDs (e.g., jobId, userId) exist in the database.
- **500 Internal Server Error**: Check logs for stack traces; common causes include database connection issues or role mismatches.
- **Failed to Load API Definition (401)**: Ensure `SecurityConfig` permits `/v3/api-docs/**` and `/swagger-ui/**`. Verify `JwtAuthenticationFilter` bypasses these paths.
- **NoSuchMethodError**: Indicates a version mismatch. Rebuild with `mvn clean install` or align Spring Boot, Spring Security, and Springdoc versions (e.g., use Springdoc 2.6.0 with Spring Boot 3.5.6).

### Logs
- Enable debug mode by adding `--debug` to `mvn spring-boot:run` for detailed error reports.

---

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/new-feature`).
3. Commit changes (`git commit -m "Add new feature"`).
4. Push to the branch (`git push origin feature/new-feature`).
5. Open a pull request.

---

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

## Acknowledgments

- Inspired by Spring Boot tutorials and community resources.
