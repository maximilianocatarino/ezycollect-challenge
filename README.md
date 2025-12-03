## 💳  Payment Processor
This application is a resilient credit card payment processing service built with Spring Boot 4 (Java 21), MongoDB, and decoupled using AWS SNS/SQS (simulated with LocalStack).

## 🚀 Features
Payment Creation: Endpoint to register payments, encrypting card numbers upon storage.

Webhook: Dynamically subscribe webhooks to payment events.

The dispatch process is robust, using SNS (payment_settled_topic) and SQS (queue_webhooks and pending_webhooks) to ensure every subscriber receives the event.

Decoupled Workflow: After registering the payment, it uses an SNS topic to queue a message to SQS (queue_webhooks).

The SQS (queue_webhooks) listener adds all subscribed webhooks to the pending_webhooks queue.

The SQS (pending_webhooks) listener processes all messages, sending the notifications to the registered endpoint.

Security: Card numbers are encrypted using AES/GCM before persistence in MongoDB.

API Documentation: OpenAPI specification (api-api.yaml) is provided in the root folder.

## 🛠 Prerequisites
- Java 21
- Apache Maven
- Docker and Docker Compose

## 🏃 How to Run
Use Docker Compose to launch the MongoDB database, LocalStack environment, which will automatically create the required SNS Topics and SQS Queues, and the application.

At first, all images will be downloaded, and the application will be built. It could take a while.

Run on your system prompt:
```
docker-compose up
```
Alternatively, you can run the application directly from the command line using Maven, which will automatically connect to the Dockerized services.
```
./mvnw spring-boot:run
```

## 🌐 API Endpoints
The API is available at http://localhost:8080/v1. Refer to open-api.yaml for full documentation.

### 1. Register a Webhook
Before creating a payment, register a webhook.

Since we need a live endpoint, you can use a service like RequestBin or run a simple local echo server.
For convenience, I have created an endpoint POST /v1/webhooks/check that logs a message and can be seen on the container output.

Endpoint: POST http://localhost:8080/v1/webhooks/payment

Payload example:
```
{
    "targetUrl": "http://example.com/listener"
}
```

#### 2. Create a Payment
Endpoint: POST http://localhost:8080/v1/payment

Payload example:
```
{
    "firstName": "Jane",
    "lastName": "Doe",
    "zipCode": "90210",
    "cardNumber": "1234-5678-9012-3456"
}
```

## ⚙️ Workflow Observation
The POST /v1/payment request returns HTTP 201 status.

The application posts a message to the SNS topic payment_settled_topic.

The SNS topic sends a message to SQS queue_webhooks.

### Worker 1 (queue_webhooks)
Receives the payment message, fetches all webhooks subscribed on MongoDB, and posts an individual task to SQS 
pending_webhooks for each registered webhook.

### Worker 2 (pending_webhooks)
Receives the individual webhook task, executes the HTTP POST request to the target URL.

## ✨ Future Improvements and Resilience
While the current dispatch workflow is robust and decoupled, a few additions would further enhance its resilience 
and reliability.

### 🛑 Dead Letter Queue (DLQ) for Failed Webhooks
A **Dead Letter Queue (DLQ)** was not configured for the `pending_webhooks` SQS queue. A DLQ is a critical component 
for production readiness.

* **Problem:** If a webhook fails repeatedly (e.g., the target server is down, or continuously returns a 500 error), 
* the message remains in the `pending_webhooks` queue until its maximum receive count is exceeded. The message is 
* then dropped and lost.
* **Improvement:** By configuring a DLQ, any message that fails processing after a defined number of retries 
* would be automatically moved to the DLQ. This allows a different process or operator to manually inspect, fix the 
* root cause, and re-inject the failed webhooks, ensuring no event is permanently lost.

### 🧪 Automated Testing
Automated tests were not a hard requirement for this project, but implementing a comprehensive test suite is always 
a good practice.

* **Improvement:** Adding unit tests and **integration tests** (especially around the LocalStack SNS/SQS interactions and the MongoDB persistence layer) would ensure that refactoring or dependency upgrades do not break the core asynchronous workflow, providing confidence in the system's reliability.

## 🛠️ AI Tools
For this project was used the IA tools Google Gemini for the initial project construction and the Github Copilot for
code assistance and completion.

The used Google Gemini prompt was:
```
Consider the coding task above to create a simple payment application. Use Java 21 and the latest version of Spring Boot 3, MongoDB as a database, and use Maven.

This application should be able to allow registering dynamic webhooks via an API, which will receive the corresponding endpoint HTTP to make a POST 
request.

Also, the application should provide an API to create a payment, accepting first name, last name, zip code, and card number. The card number should be 
encrypted when stored. Create a version 1 endpoint to path POST /payment that validates the payload, if the data is correct, save the data on payments document collection and post a message with payment data to an AWS SNS topic payment_settled_topic.

Create a worker to AWS SQS queue_webhooks, that is subscribed to the SNS payment_settled_topic, that will fetch MongoDB for webhooks on webhooks collection and post individual tasks in SQS pending_webhooks.

Create a second worker to AWS SQS pending_webhooks, that will process the message and sent a POST request to the target URL, using the payment data as payload.

For API documentation, create an OpenAPI Specification and store it with examples at the root of your project. Ensure proper return codes and meaningful information.

Create a docker-compose.yml file to run AWS SNS and SQS on LocalStack, create a shell script to automate the creation of the SNS topic and SQS queues.

Include instructions on how to run the application in a README.md file.
```