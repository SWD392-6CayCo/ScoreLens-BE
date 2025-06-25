# Kafka Security Configuration

## Overview
Kafka configuration đã được cập nhật để sử dụng environment variables thay vì hardcode sensitive information.

## Security Improvements

### Before (❌ Insecure)
```java
props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-5c346d1-kafka-scorelens.f.aivencloud.com:26036");
props.put("ssl.truststore.password", "123456");
props.put("ssl.keystore.password", "123456");
props.put("ssl.key.password", "123456");
```

### After (✅ Secure)
```java
props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
props.put("ssl.truststore.password", truststorePassword);
props.put("ssl.keystore.password", keystorePassword);
props.put("ssl.key.password", keyPassword);
```

## Setup Instructions

### 1. Environment Variables
Create `.env` file in project root (copy from `.env.template`):

```bash
# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=kafka-5c346d1-kafka-scorelens.f.aivencloud.com:26036
KAFKA_GROUP_ID=scorelens-group
KAFKA_SSL_TRUSTSTORE_LOCATION=certs/client.truststore.jks
KAFKA_SSL_TRUSTSTORE_PASSWORD=your-actual-password
KAFKA_SSL_KEYSTORE_LOCATION=certs/client.keystore.p12
KAFKA_SSL_KEYSTORE_PASSWORD=your-actual-password
KAFKA_SSL_KEY_PASSWORD=your-actual-password
```

### 2. Application Configuration
Add to `application.yml`:

```yaml
kafka:
  bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
  group-id: ${KAFKA_GROUP_ID}
  ssl:
    truststore:
      location: ${KAFKA_SSL_TRUSTSTORE_LOCATION}
      password: ${KAFKA_SSL_TRUSTSTORE_PASSWORD}
    keystore:
      location: ${KAFKA_SSL_KEYSTORE_LOCATION}
      password: ${KAFKA_SSL_KEYSTORE_PASSWORD}
    key:
      password: ${KAFKA_SSL_KEY_PASSWORD}
```

### 3. Production Deployment

#### Docker
```dockerfile
ENV KAFKA_BOOTSTRAP_SERVERS=your-production-servers
ENV KAFKA_SSL_TRUSTSTORE_PASSWORD=your-production-password
# ... other env vars
```

#### Kubernetes
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: kafka-secrets
data:
  KAFKA_SSL_TRUSTSTORE_PASSWORD: <base64-encoded-password>
  KAFKA_SSL_KEYSTORE_PASSWORD: <base64-encoded-password>
  KAFKA_SSL_KEY_PASSWORD: <base64-encoded-password>
```

#### Render/Heroku
Set environment variables in platform dashboard:
- `KAFKA_BOOTSTRAP_SERVERS`
- `KAFKA_SSL_TRUSTSTORE_PASSWORD`
- `KAFKA_SSL_KEYSTORE_PASSWORD`
- `KAFKA_SSL_KEY_PASSWORD`

## Security Best Practices

### ✅ Do
- Use environment variables for all sensitive data
- Keep `.env` file in `.gitignore`
- Use different passwords for different environments
- Rotate passwords regularly
- Use secrets management in production (AWS Secrets Manager, Azure Key Vault, etc.)

### ❌ Don't
- Hardcode passwords in source code
- Commit `.env` files to version control
- Use same passwords across environments
- Share passwords in plain text

## Files Modified
- `KafkaConsumerConfig.java` - Added @Value annotations
- `KafkaProducerConfig.java` - Added @Value annotations
- `.env.template` - Template for environment variables
- `application-kafka-template.yml` - Template for application config

## Verification
1. Check that no hardcoded passwords exist in source code
2. Verify `.env` is in `.gitignore`
3. Test that application starts with environment variables
4. Confirm Kafka connection works with new configuration

## Troubleshooting

### Common Issues
1. **Missing environment variables**: Check `.env` file exists and variables are set
2. **Wrong certificate paths**: Verify `certs/` directory and file permissions
3. **Connection timeout**: Check bootstrap servers URL and network connectivity
4. **SSL handshake failure**: Verify certificate passwords and validity

### Debug Commands
```bash
# Check environment variables
echo $KAFKA_BOOTSTRAP_SERVERS

# Test Kafka connection (if kafka tools installed)
kafka-console-consumer --bootstrap-server $KAFKA_BOOTSTRAP_SERVERS --topic test --consumer.config client.properties
```
