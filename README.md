# blog-platform-clone
A full-featured blog website inspired by Medium, built with Spring Boot. Includes user authentication (OAuth2 and email), topic following, and a modern editor for writing and publishing stories. Uses H2 database for easy setup and testing. Perfect for learning or as a starter for your own blogging platform!

# Features
User Authentication:
Sign up and sign in using email and password.
Social login with Google and GitHub (OAuth2).
Content Creation:
A "Write a story" page (/new-story) with a simple editor.
Topic Management:
Users can follow and unfollow topics.
Topics are dynamically loaded on the welcome page.
Personalized Feed:
The welcome page displays topics tailored to the user.

# Technologies Used
Backend:
Java 17
Spring Boot
Spring Security (including OAuth2 for social logins)
Spring Data JPA (Hibernate)
Maven for dependency management
Frontend:
Thymeleaf for server-side template rendering
HTML5
CSS3
JavaScript
Database:
H2 In-Memory Database

## Project Structure

demonew/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/
    │   │   └── com/example/clonemedium/
    │   │       ├── config/       # Spring Security and OAuth2 configuration
    │   │       ├── controller/   # Spring MVC controllers for handling web requests
    │   │       ├── dto/          # Data Transfer Objects
    │   │       ├── model/        # JPA Entities (User, Topic)
    │   │       ├── repository/   # Spring Data JPA repositories
    │   │       └── service/      # Business logic services
    │   └── resources/
    │       ├── static/         # CSS, JavaScript, and images
    │       ├── templates/      # Thymeleaf templates
    │       └── application.properties # Application configuration
    └── test/

# Screenshots

![index](https://github.com/user-attachments/assets/4a72e475-0b88-43af-b8b1-97cd527cb3f4)
![signup](https://github.com/user-attachments/assets/f9d4654d-d5f9-44e0-9c00-e966f0ec139e)
![signin](https://github.com/user-attachments/assets/4ea79d7e-bfb9-4e52-bbd1-af3f89f258b2)
![main](https://github.com/user-attachments/assets/6e0215ec-edc4-4169-af51-9d9d2e67d219)
![write](https://github.com/user-attachments/assets/c49dc2fc-fdd6-4efa-b4b9-a044f28e03d3)
![search](https://github.com/user-attachments/assets/6e2ee786-2f53-478a-a5f1-ab43b7b426dd)





