# Serverless URL Shortener (MySQL + Redis)

A production-grade, internet-scale URL Shortener built with **Java 17**, **Spring Boot 3**, **MySQL**, and **Redis**.

## 🚀 Architecture

*   **Compute**: AWS Lambda (Java 17 + SnapStart).
*   **Database**: **MySQL** (RDS/Aurora) for persistent storage.
*   **Cache**: **Redis** (ElastiCache) for high-performance reads (<10ms).
*   **ID Generation**: Twitter Snowflake (Distributed 64-bit ID).
*   **Encoding**: Base62 (Short and URL-friendly).

## 🛠 Tech Stack

*   **Java 17**
*   **Spring Boot 3.2**
*   **Spring Data JPA** (Hibernate)
*   **Spring Data Redis** (Caching)
*   **MySQL Connector**
*   **AWS Serverless Java Container**
*   **Lombok**

## 📦 Project Structure

```
src/main/java/com/systemdesign/urlshortener
  ├── config/          # Bean Configs
  ├── controller/      # REST API endpoints
  ├── service/         # Business logic (Caching + DB)
  ├── model/           # JPA Entities
  ├── repository/      # Data Access Layer
  ├── component/       # Snowflake ID Generator
  ├── util/            # Base62 Encoder
  └── StreamLambdaHandler.java # AWS Lambda Entry Point
```

## ⚙️ Key Features

*   **Shorten URL**: Generates unique short IDs using Snowflake + Base62.
*   **Redirection**: fast 302 redirects with Redis Caching.
*   **Caching Strategy**: Read-through/Write-through caching.
*   **Expiry**: Validates expiration dates (Lazy expiration on read).

## 🏃‍♂️ How to Run Locally

1.  **Prerequisites**: Java 17, Maven, MySQL, Redis.
2.  **Infrastructure**:
    *   Start MySQL (Port 3306).
    *   Start Redis (Port 6379).
3.  **Run**:
    ```bash
    mvn spring-boot:run
    ```
4.  **Test**:
    ```bash
    # Shorten
    curl -X POST localhost:8080/api/v1/urls -d '{"longUrl":"https://google.com"}' -H "Content-Type: application/json"
    
    # Redirect
    curl -v localhost:8080/{shortId}
    ```

## ☁️ How to Deploy (AWS)

1.  **Prerequisites**: AWS CLI, SAM CLI.
2.  **Environment Variables**:
    *   Configure `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (and Redis host if external).
3.  **Build & Deploy**:
    ```bash
    sam build
    sam deploy --guided
    ```
