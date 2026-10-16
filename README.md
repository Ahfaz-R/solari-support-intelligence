# Solari Support Intelligence

A proof of concept for searching and retrieving customer support tickets using PostgreSQL full-text search, a Spring Boot REST API, and an Angular frontend.

## Technology Stack

- Java 23 and Spring Boot
- PostgreSQL, Spring Data JPA, and Flyway
- Angular
- REST APIs for importing and searching support tickets

## Project Structure

- `backend/` — Spring Boot application, database migrations, and sample tickets
- `frontend/` — Angular application
- `.gitignore` — Excludes generated files and local secrets

## Prerequisites

Java 23, Node.js, npm, and a local PostgreSQL instance.

## Run the Backend

Create a PostgreSQL database named `solari` if necessary. Configure the username and password for your local PostgreSQL installation.

From the `backend` directory, run:

`./mvnw spring-boot:run`

The default database URL is `jdbc:postgresql://localhost:5432/solari`. Override `SPRING_DATASOURCE_URL`, `POSTGRES_USER`, and `POSTGRES_PASSWORD` using environment variables when needed.

The backend starts at `http://localhost:8080`. Flyway runs database migrations at startup.

## Import Sample Tickets

With the backend running, execute this command from the `backend` directory:

`curl -X POST http://localhost:8080/api/v1/tickets/import -H "Content-Type: application/json" --data-binary @data/tickets.json`

## Run the Frontend

From the `frontend` directory, run `npm ci` and then `npm start`.

Open `http://localhost:4200`. The Angular development proxy forwards API requests to the backend.

## Build and Test

Backend: `./mvnw clean verify` from `backend/`.

Frontend: `npm test -- --watch=false` and `npm run build` from `frontend/`.

## AI Configuration

The backend defaults to the `rules` provider. AI-related settings include `SOLARI_AI_PROVIDER`, `GOOGLE_API_KEY`, and `SOLARI_AI_MODEL`. Supply real API keys through local environment variables; never commit them.

## Project Status

This is a proof of concept, not a production-ready deployment. Assess authentication, authorization, data privacy, and operational security before using real customer data.
