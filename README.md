# DistributedFileShare

A Java console application built with Maven.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

## Building the Project

To compile the project:

```bash
mvn clean compile
```

To create an executable JAR:

```bash
mvn clean package
```

## Running the Application

### Option 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.distributedfileshare.App"
```

### Option 2: Using the JAR file

After building the JAR:

```bash
java -jar target/DistributedFileShare-1.0-SNAPSHOT.jar
```

### Option 3: Running directly from source

```bash
mvn compile exec:java -Dexec.mainClass="com.distributedfileshare.App"
```

## Project Structure

```
DistributedFileShare/
├── pom.xml
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── distributedfileshare/
                    └── App.java
```

## Development

The main entry point is `com.distributedfileshare.App`. Modify this class to add your application logic.




