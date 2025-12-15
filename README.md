<h1 align="center">
   Mockify - API Mock
</h1>

<p align="center"> 
  <img src="https://github.com/user-attachments/assets/10fc0df4-01d2-4908-a9f0-323cee98e35c" alt="Mockift" width="400"/>
</p>

<div align="center">
  <img src="https://img.shields.io/badge/Java-2a2438?style=for-the-badge&logo=openjdk&logoColor=9a8cff" />
  <img src="https://img.shields.io/badge/Spring_Boot-352f44?style=for-the-badge&logo=spring-boot&logoColor=8affc1" />
  <img src="https://img.shields.io/badge/Spring_Security-3e3a5c?style=for-the-badge&logo=spring-security&logoColor=6affb7" />
  <img src="https://img.shields.io/badge/JWT-1f1d36?style=for-the-badge&logo=jsonwebtokens&logoColor=c5c9ff" />
  <img src="https://img.shields.io/badge/Redis-2d1e3e?style=for-the-badge&logo=redis&logoColor=ff6b81" />
  <img src="https://img.shields.io/badge/Next.js-1c1f3a?style=for-the-badge&logo=next.js&logoColor=e6e9ff" />
  <img src="https://img.shields.io/badge/React-242857?style=for-the-badge&logo=react&logoColor=61DAFB" />
  <img src="https://img.shields.io/badge/TailwindCSS-2b3a55?style=for-the-badge&logo=tailwind-css&logoColor=7dd3fc" />
  <img src="https://img.shields.io/badge/Docker-1f2a44?style=for-the-badge&logo=docker&logoColor=8bbcff" />
  <img src="https://img.shields.io/badge/Docker_Compose-1f2a44?style=for-the-badge&logo=docker&logoColor=8bbcff" />
</div>

---

**Mockify** is a client–server web application for creating, managing, and securing configurable REST API mocks to speed up development, testing, and team collaboration.

## Table of Contents

- [Features](#features)
- [Application](#application)
- [Tools Used](#tools-used)
- [Installation](#installation)
- [Areas for Improvement](#areas-for-improvement)

## Features

- **REST API Mocking**: Create and manage fully configurable mock endpoints using **Spring Boot**, allowing teams to simulate REST APIs for development and testing.

- **Advanced Mock Configuration**: Each mock can define route, HTTP method, headers, status code, content type, response body (JSON, XML, or HTML), artificial delay, expiration time, and optional JWT protection.

- **Project-Based Organization**: Users can create projects and associate multiple mocks to each one, keeping APIs structured and easy to manage.

- **User Registration & Authentication**: Authentication and authorization are handled with **Spring Security** and **JSON Web Tokens (JWT)**. Roles include **ADMIN** and **MEMBER**, with fine-grained permissions enforced server-side.

- **Role-Based Access Control**:  
  - **ADMIN** users can manage users, assign roles, and delete projects or mocks.  
  - **MEMBER** users can create and edit projects and mocks but cannot delete them.

- **Distributed Sessions & Caching**: **Redis** is used via **Spring Session** to store distributed sessions and cache mock metadata, enabling scalability and consistent authentication across instances.

- **Internationalization (i18n)**: The entire application supports multiple languages, currently English and Spanish.

- **Responsive Web Interface**: The frontend, built with **Next.js**, **React**, and **Tailwind CSS**, provides a clean and responsive UI for CRUD operations on projects, mocks, and users.

- **Secure Client–Server Communication**: Sensitive routes on the frontend are protected using JWT stored in **HTTP-only cookies**.

- **Containerized Deployment**: The entire system is containerized using **Docker** and orchestrated with **Docker Compose**, including the Spring Boot backend, Redis, and the Next.js frontend.

- **Environment-Based Configuration**: Ports, credentials, JWT settings, and service connections are fully configurable through environment variables.

- **Tech Stack**
  - **Backend:** Java, Spring Boot (MVC, REST, Security), JWT  
  - **Cache & Sessions:** Redis (Spring Session)  
  - **Frontend:** Next.js, React, Tailwind CSS  
  - **Containerization:** Docker, Docker Compose
 
## Application

https://github.com/user-attachments/assets/394ece69-f516-4364-8239-83a00de37f96

A more extensive showcase is available in [my portfolio](https://vladimircuriel.com/posts/16_mockify/)!

## Tools Used

- ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=spring-boot&logoColor=white&style=flat-square) **Spring Boot**: Backend framework used to build the REST API that serves and executes configurable mock endpoints with validation, pagination, and internationalization.

- ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?logo=spring-security&logoColor=white&style=flat-square) **Spring Security**: Security framework responsible for authentication and authorization, enforcing role-based access control (ADMIN / MEMBER) across all endpoints.

- ![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white&style=flat-square) **JSON Web Tokens (JWT)**: Stateless authentication mechanism used for user login, role validation, and optional protection of mock endpoints.

- ![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white&style=flat-square) **Redis**: In-memory data store used with Spring Session to manage distributed sessions and cache mock metadata for scalable access.

- ![Next.js](https://img.shields.io/badge/NextJS-000000?logo=next.js&logoColor=white&style=flat-square) **Next.js**: React framework powering the frontend UI, route protection, and server-side rendering for the Mockify management interface.

- ![React](https://img.shields.io/badge/React-61DAFB?logo=react&logoColor=black&style=flat-square) **React**: Core library for building interactive components such as project lists, mock editors, and user management views.

- ![Tailwind CSS](https://img.shields.io/badge/TailwindCSS-38B2AC?logo=tailwind-css&logoColor=white&style=flat-square) **Tailwind CSS**: Utility-first CSS framework used to implement a responsive, clean interface for forms, tables, and dashboards.

- ![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white&style=flat-square) **Docker**: Containerization platform used to package the Spring Boot backend, Redis, and Next.js frontend into reproducible services.

- ![Docker Compose](https://img.shields.io/badge/Docker_Compose-2496ED?logo=docker&logoColor=white&style=flat-square) **Docker Compose**: Orchestration tool defining and running the multi-container Mockify stack in development and deployment environments.

## Installation

### Prerequisites

- **Docker**

### Steps

1. **Clone the repository**:

```bash
git clone https://github.com/vladimircuriel/mockify
```

2. **Navigate to the project directory**:

```bash
cd mockify
```
   
3. **Run the services**:

```bash
docker compose up -d
```

4. **Access the application**:

Open your browser and visit `http://localhost:3000` to access the user interface.

## Areas for Improvement

- **No public mock creation**: Only registered users can create mocks; there is no anonymous or public mode for quickly generating temporary mocks.

- **Mocks cannot be deleted**: Mocks are soft-disabled only. Even ADMIN users cannot permanently delete them, which may lead to long-term data accumulation.

- **Limited lifecycle control**: While mocks can be edited and deactivated, there is no archival or cleanup mechanism for inactive or expired mocks.

- **No project deletion**: Projects cannot be removed once created, and mocks cannot be reassigned between projects, reducing flexibility in long-running environments.

- **Single-response mocks only**: Each mock supports only one static response. Conditional responses based on request headers, body, or query parameters are not supported.

- **No request validation**: Incoming request payloads are not validated against schemas, which limits realism when simulating stricter APIs.

- **No abuse protection**: There is no rate limiting or throttling mechanism to protect mock endpoints from excessive or malicious traffic.

- **Limited security controls**:  
  - JWT tokens cannot be revoked before expiration.  
  - There is no validation of potentially malicious content in user-defined mock responses.

- **Lack of auditability**: The system does not keep audit logs tracking who created, modified, or deactivated mocks.

- **No monitoring or metrics UI**: Although the architecture is prepared for scalability, there are no built-in metrics or dashboards available for ADMIN users to monitor usage or system health.

- **Scalability not active**: Horizontal scaling is part of the design but is not currently enabled or observable in runtime behavior.
