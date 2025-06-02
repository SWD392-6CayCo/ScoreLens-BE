![image](https://github.com/user-attachments/assets/14048f6a-d091-441e-b198-1f9249425e70)

# 📸 ScoreLens Backend

This is the **backend service** for **ScoreLens**, an AI-powered scoring system for Pool and Carom Billiards.
All API endpoints are exposed and testable via Swagger.

🔗 **Swagger UI**: [https://scorelens-be.onrender.com/swagger-ui.html](https://scorelens-be.onrender.com/swagger-ui.html)

---

## 📚 Features

### ✅ Customer Management

| Feature                   | Description                                                                           |
| ------------------------- | ------------------------------------------------------------------------------------- |
| **Create Customer**       | Create a new customer with validation rules (email, phone, password, date of birth).  |
| **Update Customer**       | Update an existing customer (excluding email), with input validation.                 |
| **Get All / By ID**       | Retrieve full customer list or details of a specific customer by ID.                  |
| **Delete Customer**       | Remove a customer from the system permanently.                                        |
| **Lock Customer Account** | Prevent a customer from logging into the system (account status changed to *locked*). |

---

## ✍️ Customer API Validation Rules

### 📌 **Create Customer**

* **Email** must follow correct domain formats:

  ```
  @gmail.com | @yahoo.com.vn | @outlook.com.vn | @hocmai.vn | @fpt.edu.vn | @vnpt.vn
  ```
* **Phone Number** must:

  * Start with `0`
  * Contain exactly **8 or 10 digits**
* **Password** must:

  * Not be empty
  * Contain **at least 6 characters**
* **Date of Birth (dob)** must:

  * Be in the past
  * Follow format: `dd-MM-yyyy`

---

### ✏️ **Update Customer**

* ❌ **Email cannot be updated**
* ✅ Other fields follow the same rules as above:

  * Phone number, password, and dob validations still apply

---

### 🔍 **Read / Retrieve**

* **GET All Customers** – Returns all registered customers
* **GET Customer by ID** – Returns details of a specific customer by `customerId`

---

### ❌ **Delete Customer**

* Completely removes the customer from the database

---

### 🔒 **Lock Account**

* Changes customer status to `"locked"`
* Locked customers **cannot log in**

---

## 🚀 Technologies Used

* Spring Boot
* Spring Security
* Hibernate / JPA
* PostgreSQL (or your DB of choice)
* MapStruct
* Swagger for API documentation

---

## 🛠️ Setup Instructions

1. Clone the repo
2. Configure your database in `application.yml` or `.properties`
3. Run the application using:

   ```bash
   mvn spring-boot:run
   ```
4. Open Swagger: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

Let me know if you also want a version in **Vietnamese** or need sections like `Contributors`, `License`, or `Deploy Notes`.
