
# Spring Boot Keycloak Role-Based Authentication

It uses Keycloak for role-based authentication and Swagger UI for API documentation.


## Features
- Keycloak Role-Based Authentication: Role-based access control with public and private API endpoints.
- Public endpoints can be accessed by anyone.
- Private endpoints require authentication with an access token.
- Swagger UI: Access API documentation for testing and FE development

## Role Based Endpoints
- Public Endpoint: Accessible without authentication.
   - GET http://localhost:8081/public/api


- Private Endpoint: Requires authentication and valid access token.
    - GET http://localhost:8081/private/api
        (can access by ADMIN and USER)
    - GET http://localhost:8081/private/user
        (can access by USER only)
    - GET http://localhost:8081/private/admin
        (can access by ADMIN only)

## Swagger UI Endpoint
- BaseURL http://localhost:8081/swagger-ui/index.html

  - To access private endpoints in Swagger UI, you need to authenticate using the access token from Keycloak