# lead-processor-service
A Spring Boot SQS consumer that runs on ECS Fargate. It listens to the lead-processing-queue, picks up lead events published by the API service, calls an external/downstream API for business logic, and updates the lead status in PostgreSQL (SUCCESS or FAILED). It handles retries automatically via SQS visibility timeout (max 3 attempts before DLQ).
