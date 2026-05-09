# Project Structure

## Overall Structure

This repository pair is organized as a two-part applied engineering software system for smart infrastructure:

- `ansteel-shm-backend`: backend service, API delivery, persistence, and documentation
- `ansteel-shm-frontend`: frontend views, data presentation, and API consumption

## Backend Repository Role

The backend repository is responsible for:

- Spring Boot service startup and runtime
- MyBatis data access
- MySQL persistence
- domain controllers, services, mappers, DTOs, VOs, and entities
- backend-side documentation and delivery artifacts

## Frontend Repository Role

The frontend repository is responsible for:

- Vue page rendering
- monitoring page display
- API request integration
- operational visualization for raw device data and legacy monitoring modules

## Supporting Directories

- `docs/`: project-level documentation, acceptance notes, organization indexes, SQL notes, and integration writeups
- `api/`: API delivery artifacts, including OpenAPI or Apifox-compatible JSON
- `docs/sql/`: SQL scripts and SQL execution notes
- `api/apifox/`: OpenAPI / Apifox JSON exports for import and sharing

## Real Device Access V1 Archive Location

The real device raw data access preparation V1 is archived in the backend repository under:

- `docs/device-integration/`
- `docs/sql/`
- `api/apifox/`

This archive contains:

- module notes
- final acceptance summary
- SQL execution notes
- OpenAPI JSON for the 9 raw-data interfaces

