# Rental Management System

## Project Overview
The project involves developing a comprehensive Rental Management System designed to enhance and streamline rental operations for various user roles. Building upon the previous A1, the system introduces five distinct user types: Visitor, Tenant, Host, Owner, and Manager, each with specific functionalities tailored to their responsibilities. 

## Features
Visitors can browse and filter available rental properties without the need to log in. 

Tenants have the ability to manage their personal information, view rental agreements, and access payment details. 

Hosts can oversee their properties, modify rental agreements, track unpaid payments, and access property-specific statistics. 

Owners are empowered to add new properties, including uploading images, ensuring efficient storage and display within the system. 

Managers possess full control, enabling CRUD (Create, Read, Update, Delete) operations on all entities and access to comprehensive system-wide statistics. 

## Technologies Used

- **Programming Language**: Java
- **Frameworks**: JavaFX (with Scene Builder)
- **Database Management**: PostgreSQL 
- **Database Hosting**: Neon 
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **Version Control**: Git and GitHub
- **SDK**: Cloudinary 

## Setup Instructions

### Prerequisites

- **Java Development Kit (JDK) 11 or Higher**
  - [Download JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- **Apache Maven**
  - [Download Maven](https://maven.apache.org/download.cgi)
  - [Installation Guide](https://maven.apache.org/install.html)
- **PostgreSQL Database** // Put later
- **Git**
  - [Download Git](https://git-scm.com/downloads)
- **Integrated Development Environment (IDE)**
  - **Recommended**: IntelliJ IDEA, Eclipse, or VS Code with Java extensions

### Clone the Repository

Run the following commands in your terminal to clone the repository and navigate into the project directory:

`git clone https://github.com/TanNguyen17/Rental-Management-System`

`cd rental-management-system`

### Build the Project

Run the following command to build the project:

`mvn clean install`

### Run the Application

Start the application with the following command:

`mvn javafx:run`

## Branching Strategy

We follow a structured branching strategy to facilitate collaboration among our four team members.

- **Main Branch (`main`)**
- **Development Branch (`dev`)**
- **Feature Branches (`feature/{username}-{task}`)**


## Report
https://drive.google.com/file/d/1hJM9WzyUtLU19-9PV0oD609yWkpi_Wam/view?usp=sharing

## Video Demonstration

https://youtu.be/a1C-4T2JEJk

## GitHub Repository

https://github.com/TanNguyen17/Rental-Management-System

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
