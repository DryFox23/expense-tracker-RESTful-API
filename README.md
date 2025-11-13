# Expense Tracker RESTful API

A RESTful API built with Java for managing personal expenses. This project demonstrates CRUD operations, implements JWT authentication with a semi-stateless model, and features automated daily cleanup of expired tokens for enhanced security.

## Features

- Create, read, update, and delete expense records
- RESTful endpoints for easy integration
- JWT authentication with a semi-stateless token model
- Automatic deletion of expired tokens in the database every day at 12:00 AM
- Designed for SQL database integration
- Simple error handling

## Technologies Used

- Java
- (Intended for) SQL Databases (e.g., MySQL, PostgreSQL)
- Build tool: (e.g., Maven or Gradle)
- IDE: (e.g., IntelliJ IDEA)
- JWT (JSON Web Token) for authentication

## Getting Started

### Prerequisites

- Java 17+
- SQL database (MySQL, PostgreSQL, etc.)
- Build tool (Maven/Gradle)

### Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DryFox23/expense-tracker-RESTful-API.git
   ```
2. **Configure your environment variables for sensitive information:**
   - Set the following variables in your environment or in `src/main/resources/application.properties`:
     - `DATABASE_URL` — URL for your SQL database
     - `DATABASE_USERNAME` — Database username
     - `DATABASE_PASSWORD` — Database password
     - `JWT_SECRET` — Secret key for JWT token signing

   Example (`.env` or directly in your shell environment):
   ```
   DATABASE_URL=jdbc:mysql://localhost:3306/expense_db
   DATABASE_USERNAME=root
   DATABASE_PASSWORD=yourpassword
   JWT_SECRET=your_jwt_secret
   ```

3. **Build the project:**
   ```bash
   # For Maven:
   mvn clean install
   # For Gradle:
   gradle build
   ```

4. **Run the application:**
   ```bash
   # Example for Spring Boot (adjust as needed)
   mvn spring-boot:run
   ```

## API Endpoints

- `GET /expenses` - List all expenses
- `GET /expenses/{id}` - Get expense by ID
- `POST /expenses` - Create a new expense
- `PUT /expenses/{id}` - Update an expense
- `DELETE /expenses/{id}` - Delete an expense

## Authentication

This API uses JWT authentication with a semi-stateless model:
- Tokens are stored in the database.
- Each API request is authenticated via JWT.
- Expired tokens are automatically deleted from the database every day at 12:00 AM, increasing security and maintaining session hygiene.

## Usage

You can use tools like Postman or curl to interact with the API.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

This project is licensed under the MIT License.
