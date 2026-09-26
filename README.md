# Bankslip

Bankslip is a Spring Boot service that accepts bank-slip requests over HTTP, queues them with SQS, then generates a barcode and PDF in a background consumer. If a webhook URL is supplied, the service posts the result there.

## Requirements

- Java 21
- PostgreSQL, with a database named `bankslip`
- AWS SQS, or LocalStack with SQS enabled
- AWS CLI for the LocalStack queue setup below

The application connects to `jdbc:postgresql://localhost:5432/bankslip` with the local credentials `postgres` / `postgres` by default. Start PostgreSQL and create the database before starting the application. Spring SQL initialization runs [schema.sql](src/main/resources/schema.sql) at startup and creates the tables if they do not already exist. It does not migrate existing tables.

## Local setup

Start LocalStack. The Compose file uses `LOCALSTACK_AUTH_TOKEN` from the environment if required by the LocalStack image:

```sh
docker compose up -d localstack
```

Create a standard queue for the current producer, which does not set a FIFO message-group ID:

```sh
aws --endpoint-url=http://localhost:4566 sqs create-queue \
  --queue-name bank-slip-queue
```

Start the service using that queue:

```sh
BANKSLIP_SQS_QUEUE_URL=bank-slip-queue ./gradlew bootRun
```

The checked-in default queue name is `bank-slip-queue.fifo`. For a FIFO queue, the producer must supply a message-group ID; configure that support before using a FIFO queue.

## Create a bank slip

Send `POST /api/bank-slips` with the required `X-API-Version: 1` header. All fields below except `webhookUrl` are required. `amount` must be greater than zero; `dueDate` uses ISO `YYYY-MM-DD` format.

```sh
curl -X POST http://localhost:8080/api/bank-slips \
  -H 'Content-Type: application/json' \
  -H 'X-API-Version: 1' \
  -d '{
    "customerId": "customer-123",
    "payerName": "Example Payer",
    "payerDocument": "payer-document-number",
    "beneficiaryName": "Example Beneficiary",
    "beneficiaryDocument": "beneficiary-document-number",
    "documentNumber": "invoice-456",
    "amount": 125.50,
    "dueDate": "2026-10-15",
    "webhookUrl": "https://example.com/hooks/bank-slips"
  }'
```

The service responds with `202 Accepted` after enqueueing the request:

```json
{
  "requestId": "generated-uuid",
  "status": "accepted"
}
```

## Webhook

When processing completes and `webhookUrl` is non-empty, the service posts a JSON body in this shape:

```json
{
  "payload": {
    "id": 42,
    "customerId": "customer-123",
    "barcode": "generated-barcode",
    "digitableLine": "generated-digitable-line",
    "amount": 125.50,
    "dueDate": "2026-10-15",
    "base64file": "base64-encoded-pdf"
  },
  "error": null
}
```

The callback's `payload.id` is the database ID. The `requestId` returned by the HTTP endpoint is not currently included in the queued message or webhook payload, so it cannot be used to correlate the callback with the original response.

## Configuration

The application defaults are in `src/main/resources/application.properties`:

| Property | Default |
| --- | --- |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/bankslip` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `postgres` |
| `spring.cloud.aws.region.static` | `us-east-1` |
| `spring.cloud.aws.sqs.endpoint` | LocalStack endpoint |
| `bankslip.sqs.queue-url` | `bank-slip-queue.fifo` |

The checked-in AWS credentials (`test` / `test`) are for local development only. Override the datasource, AWS region, credentials, SQS endpoint, and `BANKSLIP_SQS_QUEUE_URL` for other environments. For AWS-hosted SQS, remove or override the LocalStack endpoint setting.

## Tests

Run the unit tests with:

```sh
./gradlew test
```

## Current limitations

- The listener and webhook handling catch processing exceptions, so failures are not reliably surfaced to SQS for redrive-policy retries. Do not rely on a dead-letter queue for recovery until exceptions are propagated correctly.
- FIFO publishing is not fully configured: the producer currently does not set a message-group ID.
- The accepted `requestId` is not propagated to the webhook; the webhook currently identifies the persisted record by its numeric database ID.
