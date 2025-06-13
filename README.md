# 📱 Device API

A RESTful API built with Java 21 and Spring Boot to manage device resources. It supports full CRUD operations, domain validations, and is ready for production with test coverage, OpenAPI documentation, PostgreSQL persistence, and containerization using Docker.

---

## 🚀 Technologies Used

- Java 21+
- Spring Boot 3.x
- Maven 3.9+
- PostgreSQL (Prod)
- H2 (Test profile)
- Testcontainers
- JUnit 5
- MockMvc
- OpenAPI/Swagger
- Docker / Docker Compose

---

## 📦 Project Structure

```
src/
 └── main/
     ├── java/com/example/deviceapi
     │   ├── controller
     │   ├── dto
     │   ├── entity
     │   ├── exception
     │   ├── repository
     │   ├── service
     │   └── Application.java
     └── resources/
         ├── application.yml
         └── schema.sql
 └── test/
     └── java/com/example/deviceapi
         ├── service/DeviceServiceTest.java
         └── controller/DeviceControllerIT.java
```

---

## 🎯 Challenge Compliance

| Requirement | Implemented |
|-------------|-------------|
| Persist data on external DB (not in-memory) | ✅ PostgreSQL |
| Create, read, update, delete devices | ✅ |
| Fetch by brand and state | ✅ |
| `creationTime` cannot be updated | ✅ |
| If `IN_USE`, name/brand cannot be updated | ✅ |
| `IN_USE` devices cannot be deleted | ✅ |
| Reasonable test coverage | ✅ `DeviceServiceTest`, `DeviceControllerIT` |
| API documentation (Swagger) | ✅ Available at `/swagger-ui.html` |
| Dockerized / Containerized | ✅ via `docker-compose up --build` |
| Delivered in Git repository | ✅ GitHub |
| Complete README | ✅ This file |

---

## 🛠️ Running the Application


### 🐳 Using Docker Compose (Production)

To start the application and the PostgreSQL database container:

```bash
git clone https://github.com/marcoseduardoqueiroz/deviceapi.git
cd deviceapi

docker-compose up --build
```

After startup, the API will be available at:

- API base URL: `http://localhost:8080/api/v1/devices`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🧪 Testing

### Run Unit Tests (`DeviceServiceTest`):

```bash
mvn test
```

### Run Integration Tests (`DeviceControllerIT` with H2 and Testcontainers):

```bash
mvn verify
```

---

## 📌 Domain Rules

- The `creationTime` field is immutable and cannot be updated.
- Devices in state `IN_USE`:
  - Cannot have `name` or `brand` updated.
  - Cannot be deleted.

---

## 🔧 Future Improvements

- Add pagination and sorting for list endpoints.
- Allow filtering by multiple criteria simultaneously.
- Implement authentication and authorization (e.g., JWT).
- Add audit logging for changes in device data.

---

## 👨‍💻 Author

Developed by Marcos Eduardo Queiroz — [GitHub Profile](https://github.com/marcoseduardoqueiroz)
