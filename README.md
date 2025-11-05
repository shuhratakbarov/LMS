# OpenLMS

**OpenLMS** is a modern Learning Management System designed to streamline academic workflows for educational institutions.  
It provides different role-based workspaces for **Administrators**, **Teachers**, and **Students**, making course and learning management simple and efficient.

---

## 🚀 Demo

**Live URL:** https://lms.shuhratakbarov.uz

Use one of the demo accounts:

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `ZzAaQq!1` |
| Teacher | `teacher` | `ZzAaQq!1` |
| Student | `student` | `ZzAaQq!1` |

---

## 🏗️ Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT Authentication & RBAC)**
- **PostgreSQL**
- **Hibernate/JPA**
- **Docker**

---

## 🎯 Core Features

### 👑 Administrator
- Manage **courses, groups, lesson schedules, users, rooms**
- Assign teachers & students to groups
- System-wide configuration & control

### 📚 Teacher
- Create and manage **exams**
- Create and review **assignments and homework**
- Evaluate student work and give feedback
- Communicate with students and groups

### 🧑‍🎓 Student
- Take exams and quizzes
- Upload homework and track evaluations
- Participate in course activities
- Communicate with teachers & classmates

### 💬 Messaging Module
- Real-time conversations across all roles

---

## 🔐 Authentication & Authorization

- **JWT-based** authentication (`username + password`)
- **Role-Based Access Control (RBAC)**:
  - `ROLE_ADMIN`
  - `ROLE_TEACHER`
  - `ROLE_STUDENT`

---

## 🧱 Architecture
```
OpenLMS (Multi-Repo)
 ├── lms/ Backend (Spring Boot REST API) 
 └── lms-frontend/ Frontend (React Client UI)
```
Backend and frontend are maintained separately to ensure flexibility and scalability.

---

## 🏃‍♂️ How to Run Locally

### Prerequisites
- Docker & Docker Compose
- Java 17
- Node.js (if running frontend manually)

### Option 1 — Run Both with Docker (Recommended)
```bash
docker-compose up --build
```
### Option 2 — Run Separately
## Backend
```bash
./mvnw spring-boot:run
```
## Frontend
```bash
npm install
npm start
```
### Database Schema Example (Simplified)
```
User ──< Enrollment >── Group ──< Course
    \                     /
     \──< Message >──────/
```
### 🤝 Contributing
Contributions are welcome!
Please open an issue or submit a pull request.
### 📄 License
This project is licensed under the MIT License.
## Made with ❤️ by Shuhrat Akbarov
