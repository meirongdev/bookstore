# BookStore - Physical Bookstore Online Management System

## 📖 Project Description

This is an online management system designed for physical bookstores, focusing on book borrowing and return process management. The system supports users borrowing books and automatically calculates and deducts overdue fees for books not returned on time.

**Project Purpose**: As a technology demonstration project, showcasing modern web application full-stack development technologies and best practices.

## ✨ Core Features

### 📚 Book Loan Management

- Rich book catalog browsing
- Book loan application and approval
- Loan history tracking

### 👤 User Management System

- User registration and authentication
- Personal loan history viewing
- Account balance management

### 💰 Fee Management

- Automatic overdue fee calculation
- Multiple payment methods integration (Stripe)
- Fee deduction and billing management

### 🔐 Security Features

- JWT authentication
- Role-based access control
- API security protection

## 🛠️ Technology Stack

### Backend (Spring Boot)

- **Framework**: Spring Boot 4.0
- **Language**: Java 25
- **Database**: PostgreSQL
- **ORM**: JPA/Hibernate
- **Migration**: Liquibase
- **Security**: Spring Security + JWT
- **Payment**: Stripe API
- **Documentation**: OpenAPI/Swagger

### Frontend (React + TypeScript)

- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **Styling**: Tailwind CSS
- **State Management**: React Context
- **Routing**: React Router

### Infrastructure

- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven
- **Version Control**: Git

## 🚀 Quick Start

### Environment Requirements

- Java 25
- Node.js 18+
- PostgreSQL 15+
- Docker & Docker Compose

### Backend Startup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Startup

```bash
cd frontend
npm install
npm run dev
```

### Docker Startup

```bash
docker-compose up -d
```

## 📁 Project Structure

```
bookstore/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/       # Java source code
│   ├── src/main/resources/  # Configuration files and database migrations
│   └── pom.xml             # Maven configuration
├── frontend/               # React frontend
│   ├── src/                # TypeScript/React source code
│   ├── public/             # Static assets
│   └── package.json        # npm configuration
├── docker-compose.yml      # Docker orchestration configuration
└── README.md              # Project documentation
```

## 🔧 Configuration

### Database Configuration

The system uses PostgreSQL database with Liquibase for database version management.

### Payment Integration

Integrated with Stripe payment gateway, supporting multiple payment methods and automatic deduction features.

### Security Configuration

Uses JWT tokens for authentication, supporting role-based access control.

## 📊 Database Design

Main entities:

- **User**: User information
- **Book**: Book information
- **BookLoan**: Loan records
- **Payment**: Payment records

## 🎯 Technical Highlights

- **Modern Tech Stack**: Using the latest Spring Boot 4.0 and Java 25
- **Type Safety**: TypeScript/Java strongly typed languages on both frontend and backend
- **Microservices Architecture**: Clear frontend-backend separation design
- **Automated Testing**: Complete unit tests and integration tests
- **CI/CD Ready**: Support for containerized deployment and automated builds

## 📝 API Documentation

After starting the backend service, access API documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Specification: `http://localhost:8080/v3/api-docs`

## 🤝 Contributing

This is a technology demonstration project, mainly for learning and demonstration purposes. To contribute, please follow these steps:

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is for technology demonstration and learning purposes only.

## Starting the Project

### Docker Compose

```bash
docker-compose up -d postgres
make migrate
make build-backend
make build-frontend
make up
```

- frontend: http://localhost:8000
- backend: http://localhost:8080
- grafana: http://localhost:3000
- prometheus: http://localhost:9090

