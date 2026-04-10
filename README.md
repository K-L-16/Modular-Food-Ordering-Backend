# Modular-Food-Ordering-Backend

A modular Spring Boot backend for a food ordering platform, built with **Java**, **Spring Boot**, **MyBatis**, **MySQL**, **Redis**, **WebSocket**, and **JWT**.

This project is organized as a multi-module backend system and includes authentication, order-related workflows, file upload support, scheduled tasks, reporting modules, and role-based request handling for both admin and user APIs.

## Tech Stack

- Java
- Spring Boot
- Restful api
- MyBatis
- MySQL
- Redis
- WebSocket
- JWT
- Swagger / Knife4j
- Maven (multi-module)
- Cloudinary

## Features
- Multi-module backend architecture
- JWT-based authentication
- Separate admin and user request interceptors
- Redis cache
- WebSocket-based message / notification support
- AOP-based auto-fill handling
- Global exception handling
- File upload integration with Cloudinary
- Scheduled task support
- Report-related backend services
- Swagger / Knife4j API documentation

## Project Structure

```text
skybite-modular-backend
├── sky-common
├── sky-pojo
└── sky-server
```

## What I Practiced in This Project
- organizing a Java backend as a multi-module Maven project
- structuring layered Spring Boot services
- implementing JWT-based request interception
- integrating Redis and WebSocket into business workflows
- using AOP for repetitive backend logic
- building backend support features such as scheduled tasks, reporting, and file upload
