FROM localstack/localstack:3.5

USER root

# Script to create resources in LocalStack
COPY docker/init-aws.sh /etc/localstack/init/ready.d/init-aws.sh
RUN chmod +x /etc/localstack/init/ready.d/init-aws.sh