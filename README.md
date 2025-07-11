# Jurnal Pribadi - Personal Journal Web Application

A web-based personal journal application built with Java Servlet/JSP technology that allows users to create, manage, and organize their personal journal entries securely.

## 📋 Features

- **User Authentication**: Secure login and registration system with password encryption
- **Journal Management**: Create, edit, delete, and view journal entries
- **Data Persistence**: SQLite database for reliable data storage
- **Web Interface**: Clean and responsive web interface using JSP
- **Session Management**: Secure session handling for user authentication

## 🛠️ Technology Stack

- **Backend**: Java Servlet/JSP
- **Database**: SQLite 3
- **Build Tool**: Maven
- **Server**: Apache Tomcat 11+
- **Java Version**: JDK 17+
- **Security**: BCrypt password hashing

## 📱 Screenshot

<img width="2541" height="1507" alt="Screenshot 2025-06-07 233116" src="https://github.com/user-attachments/assets/12ff991d-b98f-4132-82e3-e38215df1fb5" />
<img width="2535" height="1503" alt="Screenshot 2025-06-08 202331" src="https://github.com/user-attachments/assets/483a2b9c-3ddc-4d6d-8500-9b548f8c357f" />

## 📦 Prerequisites

Before running this application, make sure you have the following installed:

- **Java Development Kit (JDK) 17** or later
- **Apache Tomcat 11** or later
- **Maven** (for building the project)
- **SQLite 3** (included as dependency)

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Monarch055/Jurnal-Pribadi-Online.git
cd jurpibadi
```

### 2. Build the Project

```bash
mvn clean package
```

This will create a WAR file at `target/jurpibadi.war`

### 3. Deploy to Tomcat

1. Copy the generated WAR file to your Tomcat webapps directory:
   ```bash
   cp target/jurpibadi.war $TOMCAT_HOME/webapps/
   ```

2. Start Tomcat server:
   ```bash
   $TOMCAT_HOME/bin/startup.sh  # Linux/Mac
   # or
   $TOMCAT_HOME\bin\startup.bat # Windows
   ```

3. Access the application at: `http://localhost:8080/jurpibadi`

## 📁 Project Structure

```
jurpibadi/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── jurnalpribadi/
│       │           ├── controller/    # Servlet controllers
│       │           ├── dao/           # Data Access Objects
│       │           ├── model/         # Data models
│       │           ├── util/          # Utility classes
│       │           └── listener/      # Application listeners
│       └── webapp/
│           ├── assets/                # Static resources (CSS, JS, images)
│           ├── WEB-INF/               # Web configuration files
│           ├── index.jsp              # Main entry point
│           └── jurnal_pribadi.db      # SQLite database
├── target/                            # Compiled output
├── pom.xml                           # Maven configuration
└── README.md                         # This file
```

## 🔧 Configuration

The application uses SQLite as its database, which is automatically created when the application starts. The database file is located at `src/main/webapp/jurnal_pribadi.db`.

## 👥 Usage

1. **Registration**: Create a new account through the registration page
2. **Login**: Sign in with your credentials
3. **Create Entries**: Add new journal entries with title and content
4. **Manage Entries**: Edit, delete, or view your existing entries
5. **Logout**: Securely log out when finished

## 🔒 Security Features

- Password encryption using BCrypt
- Session-based authentication
- SQL injection prevention through prepared statements
- Input validation and sanitization


**Note**: This is a personal project developed for educational purposes. Make sure to review and modify the code according to your specific requirements before using it in production.
