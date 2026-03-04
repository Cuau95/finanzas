"# finanzas" 
"# finanzas" 

# 💰 Personal Finance API

REST API built with Spring Boot to manage personal financial records such as transactions and concurrent values.

This project is part of my continuous learning process, focusing on clean architecture, REST design, validation, testing strategies, and best practices in backend development.

---

## 📌 Overview

The API allows:

- Creating single and batch financial records
- Managing concurrent financial values
- Handling validation and business errors using `ProblemDetail`
- Applying structured exception handling with `@RestControllerAdvice`
- Testing controllers using `@WebMvcTest`

---

## 🛠 Tech Stack

- Java 17
- Spring Boot 4
- Spring Web
- Spring Data JPA
- Hibernate
- H2 (tests)
- MySQL (local environment)
- Maven
- JUnit 5
- Mockito
- MockMvc

---

## 🏗 Architecture

The project follows a layered architecture:

Controller → Mapper → Service → Repository → Database

Principles applied:

- Separation of concerns
- DTO pattern
- Centralized exception handling
- Validation with Jakarta Bean Validation
- RESTful standards
- Unit and MVC slice testing

---
