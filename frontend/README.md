# Solari Support Intelligence

A proof of concept for searching and retrieving customer support tickets using PostgreSQL full-text search, a Spring Boot REST API, and an Angular frontend.

## Architecture

- **Backend:** Java, Spring Boot, Spring Data JPA, Flyway
- **Database:** PostgreSQL with full-text search and a GIN index
- **Frontend:** Angular
- **API:** REST endpoints for importing and searching support tickets

## Project structure

```text
backend/
  src/main/java/       Spring Boot application
  src/main/resources/  Application configuration and Flyway migrations
  data/                Example support-ticket JSON files
frontend/
  src/app/             Angular application and ticket-search feature