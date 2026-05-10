# Cloud File Storage API

A real-world cloud-based file storage backend for managing authentication, folder management, file uploads, and secure access control. This project is designed as a modular monolith using production-ready Spring Boot practices, intended for deployment on Google Cloud Platform.

## Features

*   **Authentication:** Secure user registration and login using JWT and Role-Based Access Control (RBAC).
*   **File Management:** Upload, download, and delete files (images, videos, documents).
*   **Folder Organization:** Create nested folders to organize files.
*   **Activity Logging:** Audit trail for user actions like uploads, downloads, and deletions.

## Architecture

This project follows a **Modular Monolith** architecture. It is a single deployable Spring Boot application with one PostgreSQL database, but with strict separation between business domains.

*   **Domain Separation:** Each module (e.g., `auth`, `storage`, `users`) is self-contained with its own services, repositories, and entities.
*   **No Cross-Module Leaks:** Modules do not directly access each other's repositories or entities. Communication happens through well-defined interfaces and Spring's internal domain events.
*   **Scalability:** This design allows for easier evolution into microservices in the future if needed.

### Modules

*   `auth`: Handles user authentication (JWT) and authorization (RBAC).
*   `users`: Manages user profile information.
*   `storage`: Core module for file operations and integration with Google Cloud Storage.
*   `folders`: Manages folder creation, nesting, and organization.
*   `activity`: Provides an activity log for audit purposes.
*   `common`: Contains shared utilities and configurations.

## Technologies Used

*   **Backend:** Spring Boot 4
*   **Language:** Java 25
*   **Database:** PostgreSQL
*   **Cloud Provider:** Google Cloud Platform (GCP)
*   **File Storage:** Google Cloud Storage (GCS)
*   **Database Hosting:** Google Cloud SQL
*   **Containerization:** Docker
*   **Deployment:** Google Cloud Run
*   **API Documentation:** SpringDoc OpenAPI (Swagger UI)


