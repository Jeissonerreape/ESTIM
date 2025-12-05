## 🧩 Workshop 2 — Design Artifacts and System Modeling

The following document describes the first steps for formal design of the course project. It includes aesthetic design, with the first mockups, as well as systemic design, such as definition of functionality, and its underlying architecture to follow in the development phase.

📄 [**Workshop-2.pdf**](https://github.com/jujgomezru/ESTIM/blob/main/Final-Project/workshops/Workshop-2/Workshop-2.pdf)

---

### 1. CRC Cards

Definition of the main classes, their **responsibilities** and **collaborators** for the ESTIM videogame store system.  
Classes include:

- **Videogame:** manages metadata, reviews, promotions, and availability.  
- **Library:** stores purchased games and manages installations.  
- **User:** handles authentication, library, shopping cart, wishlist, and reviews.  
- **License:** validates ownership and access to games.  
- **ShoppingCart / CartItem / Order / OrderItem:** manage purchases and pricing.  
- **Promotion, Review, Wishlist, Achievement, Publisher, Administrator, SupportTicket:** support core platform features such as promotions, content moderation, and support.

---

### 2. Mockups

Wireframes for the main user interfaces:

- **Main Page:** displays featured games and offers.  
- **Library Page:** shows the user’s purchased games.  
- **Game Page:** presents details, reviews, and purchase options.

---

### 3. Business Model Processes

BPMN activity diagram of the **videogame purchase process**, illustrating the flow from game search to payment and confirmation.

---

### 4. Architecture Diagram

Layered software architecture of **ESTIM** including technologies, layers, and communication flows.

#### Technologies
- **Frontend:** React SPA (REST APIs)
- **Java Backend:** Spring Boot (controllers, services, repositories)
- **Python Backend:** FastAPI (routers, DAOs)
- **Database:** PostgreSQL with JPA/Hibernate and SQLAlchemy
- **Cache/Messaging:** Redis (cache + pub/sub)
- **Contracts:** OpenAPI, JSON Schema, AsyncAPI
- **Infrastructure:** Docker, CI/CD with GitHub Actions

#### Layers
- Presentation  
- Business Logic  
- Event Handlers  
- Data Access  
- Database  
- Shared Types  

#### Communication

Quick outline of the main communication flows of the proposed software architecture.

---

### 5. Class Diagram

UML class diagram showing the main entities and their relationships, emphasizing modularity and data encapsulation.

---

### 6. Relational Database Model

Entity-relationship model covering:
- Users, profiles, and preferences  
- Games, achievements, reviews, promotions  
- Orders, licenses, and support tickets  
- Messaging and refunds  

---

### References

- [Lucidchart — Architectural Diagrams](https://www.lucidchart.com/blog/how-to-draw-architectural-diagrams)  
- [Lucidchart — ER Diagrams](https://www.lucidchart.com/pages/er-diagrams)  
- [UML Diagrams Overview](https://www.uml-diagrams.org/class-diagrams-overview.html)  
- [Visual Paradigm — What is BPMN?](https://www.visual-paradigm.com/guide/bpmn/what-is-bpmn/)


