#!/bin/bash
set -e

# Wait for LocalStack services to be ready
echo "Waiting for LocalStack services..."
sleep 10

# Configure AWS CLI to use LocalStack endpoint
AWS_CLI="awslocal"
REGION="us-east-1"
export AWS_DEFAULT_REGION=$REGION

echo "Starting AWS resource setup..."

# --- SQS Queue Creation ---
echo "Creating SQS Queues..."
$AWS_CLI sqs create-queue --queue-name queue_webhooks
$AWS_CLI sqs create-queue --queue-name pending_webhooks

# --- SNS Topic Creation ---
echo "Creating SNS Topic: payment_settled_topic"
SNS_TOPIC_ARN=$($AWS_CLI sns create-topic --name payment_settled_topic --query 'TopicArn' --output text)
echo "SNS Topic ARN: $SNS_TOPIC_ARN"

# --- SNS Subscription ---
echo "Subscribing queue_webhooks to payment_settled_topic"

# Get the URL and ARN of the queue_webhooks
QUEUE_WEBHOOK_URL=$($AWS_CLI sqs get-queue-url --queue-name queue_webhooks --query 'QueueUrl' --output text)
QUEUE_WEBHOOK_ARN=$($AWS_CLI sqs get-queue-attributes --queue-url "$QUEUE_WEBHOOK_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

echo "Queue webhook URL: $QUEUE_WEBHOOK_URL"
echo "Queue webhook ARN: $QUEUE_WEBHOOK_ARN"

# Get the URL and ARN of the pending_webhooks
PENDING_WEBHOOK_URL=$($AWS_CLI sqs get-queue-url --queue-name pending_webhooks --query 'QueueUrl' --output text)
PENDING_WEBHOOK_ARN=$($AWS_CLI sqs get-queue-attributes --queue-url "$PENDING_WEBHOOK_URL" --attribute-names QueueArn --query 'Attributes.QueueArn' --output text)

echo "Pending webhook URL: $PENDING_WEBHOOK_URL"
echo "Pending webhook ARN: $PENDING_WEBHOOK_ARN"

# Subscribe the SQS queue to the SNS topic
$AWS_CLI sns subscribe \
    --topic-arn "$SNS_TOPIC_ARN" \
    --protocol sqs \
    --notification-endpoint "$QUEUE_WEBHOOK_ARN" \
    --attributes '{"RawMessageDelivery":"false"}'

echo "Successfully subscribed $QUEUE_WEBHOOK_ARN to $SNS_TOPIC_ARN"

echo "Apply policy to $QUEUE_WEBHOOK_URL"
QUEUE_POLICY=/app/aws-policy.json

$AWS_CLI sqs set-queue-attributes \
    --queue-url "$QUEUE_WEBHOOK_URL" \
    --attributes Policy="$(cat "$QUEUE_POLICY")"

echo "AWS resource setup complete."
