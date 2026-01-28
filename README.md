# User CRUD Service (Java + Spring Boot + MySQL + Docker)

This project implements a system to manage users together with massive import through CSV file.

## Features
- **Backend**: Spring Boot 3 / Java
- **Database**: MySQL 8
- **Containerizzazione**: Docker & Docker Compose
- **Parsing CSV**: OpenCSV

## How to launch 
1. `mvn clean package -DskipTests`
2. `docker-compose up --build`

## Main Endpoints 
- `GET /api/users/search`: Filtered research by name and surname.
- `POST /api/users/upload`: Massive import from CSV file.


