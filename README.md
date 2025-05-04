<h1 align="center">
  <img src="src/docs/assets/icon.png" width="300px" alt="App Icon"/><br/>
  <strong>Tuka's Comic Book Characters Manager</strong>
</h1>

<p align="center">
  A Java desktop application for managing comic book characters with a database-backed system.
</p>

<hr/>

## System Requirements

Ensure the following tools are installed to build and run the project:

- **[Apache Maven 3.9.9+](https://maven.apache.org/download.cgi)**: Build automation and project management tool for Java-based projects.

- **[Java Development Kit (JDK) 17+](https://www.oracle.com/au/java/technologies/downloads/)**: Required to compile and run the application.
    - Ensure `JAVA_HOME` is properly configured on your system.

- **[Git](https://git-scm.com/download/)**: Enables version control and collaborative development.
    - Git maintains strong backward compatibility, so any recent version should work fine.

- **(Optional) [PostgreSQL 16.8+](https://www.postgresql.org/download/)**: Used for setting up a local database instance.
    - Includes [pgAdmin](https://www.pgadmin.org/) for database management (bundled with standard installation).

---

## Installation Guide

### 1. Clone the repository

```
git clone https://github.com/hotungkhanh/comic-characters.git
```
```
cd comic-characters
```

### 2. Configure database connection

Create a `.env` file in the project root directory:

```properties
DB_URL={your PostgreSQL database URL}
DB_USERNAME={your PostgreSQL username}
DB_PASSWORD={your PostgreSQL password}
```

### 3. Run the application

Choose one of the following methods:

<details>
<summary><strong>Option A: Using the command line</strong></summary>

Build the application:
```bash
mvn clean package
```

Run the JAR file:
```bash
java -jar target/comic-characters-1.0-SNAPSHOT.jar
```
</details>

<details>
<summary><strong>Option B: Using an IDE</strong></summary>

1. Open the project in your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Navigate to the main class:
   ```
   src/main/java/com/tuka/comiccharacters/ui/MainApp.java
   ```
3. Run the `main()` method:

   <img src="src/docs/assets/main-app.png" alt="MainApp.java screenshot"/>
</details>

### 4. Verify installation

The application should launch displaying the main user interface. You're now ready to manage your comic character collection!


## Features

## Documentation

