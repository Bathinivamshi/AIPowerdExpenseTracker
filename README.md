
# Expense Tracker

A full-stack expense tracking application built with Spring Boot, React.js, MySQL, JWT authentication, and Docker.

## Features

- User registration and login
- JWT-based authentication
- Add, edit, and delete expenses
- View all expenses
- Search expenses
- Filter expenses by category
- Sort expenses
- Track expense dates
- Total spending statistics
- Average expense statistics
- Expense count
- Category-wise spending summary
- Docker and Docker Compose support
- Responsive React UI

## Tech Stack

### Backend
- Java 21
- Spring Boot
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- MySQL
- Maven

### Frontend
- React.js
- Vite
- JavaScript
- CSS

### DevOps
- Docker
- Docker Compose
- Git
- GitHub

## Architecture

React Frontend
|
| REST API + JWT
v
Spring Boot Backend
|
| JPA / Hibernate
v
MySQL Database


## Project Structure

ExpenseTracker/
├── src/
│ └── main/
│ ├── java/
│ └── resources/
├── frontend/
│ ├── src/
│ │ ├── components/
│ │ └── pages/
│ ├── package.json
│ └── Dockerfile
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── Readme.md


## Running Locally

### Backend

Make sure MySQL is running and configure your database connection in:

src/main/resources/application.properties


Build the backend:

mvn clean package -DskipTests


Run:

java -jar target/*.jar


Backend runs at:

http://localhost:8080


### Frontend

cd frontend
npm install
npm run dev


Frontend runs at:

http://localhost:5173


## Running with Docker

Build and start the complete application:

docker compose up --build


Frontend:

http://localhost:5173


Backend:

http://localhost:8080


Stop the application:

docker compose down


## Authentication

The application uses JWT authentication. Users can:

1. Register an account
2. Log in using their email and password
3. Receive a JWT token
4. Use the token to access protected expense APIs

Example authorization header:

Authorization: Bearer <JWT_TOKEN>


## Main API Endpoints

### Authentication

POST /api/auth/register
POST /api/auth/login


### Expenses

GET /api/expenses
POST /api/expenses
PUT /api/expenses/{id}
DELETE /api/expenses/{id}


### Statistics

GET /api/expenses/total
GET /api/expenses/average
GET /api/expenses/count


## Dashboard

The dashboard provides:

- Total amount spent
- Average expense
- Number of expenses
- Category-wise spending
- Expense search
- Category filtering
- Expense sorting

## Security

- Passwords are hashed using BCrypt
- Protected APIs require JWT authentication
- Users can access only their own expenses
- CORS is configured for the React frontend

## Future Improvements

- Expense charts and visual analytics
- Monthly expense reports
- Budget management
- Email notifications
- Cloud deployment
- Mobile application
- AI-powered spending insights

## Author

Bathinivamshi
GitHub: https://github.com/Bathinivamshi
