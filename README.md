# Bankslip

API for asynchronously creating simulated bank slips. The HTTP request is sent to SQS before the API returns `202 Accepted`. A background consumer generates a simulated barcode and posts the result to the supplied webhook URL. Webhook delivery must return a successful HTTP status for the SQS message to be deleted; failures are retried by SQS according to the queue's redrive policy.

## Create a request

`POST /api/bank-slips`

```json
{
  "customerId": "customer-123",
  "amount": 125.50,
  "dueDate": "2026-10-15",
  "webhookUrl": "https://example.com/hooks/bank-slips"
}
```

The API responds with `202 Accepted` and a request identifier:

```json
{
  "requestId": "uuid",
  "status": "accepted"
}
```

On completion, the webhook receives a JSON body with `requestId`, `status`, `customerId`, `amount`, `dueDate`, `barcode`, and `digitableLine`.

## Configuration

Set `BANKSLIP_SQS_QUEUE_URL` to an existing SQS queue URL. AWS credentials use the standard AWS SDK credential chain. Region defaults to `us-east-1`; set `AWS_REGION` to change it. For LocalStack, set `AWS_ENDPOINT_URL_SQS` to the SQS endpoint (for example `http://localhost:4566`) and configure the AWS SDK credentials and queue URL for that environment.

Start the app with `./gradlew bootRun` after configuring the queue.
