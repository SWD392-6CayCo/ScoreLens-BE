# Required Environment Variables

## Database
```bash
DB_URL=jdbc:mysql://localhost:3306/scorelens
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

## JWT
```bash
JWT_SIGNER_KEY=your_jwt_secret_key
```

## Kafka
```bash
KAFKA_GROUP_ID=scorelens-group
KAFKA_KEYSTORE_LOCATION=certs/client.keystore.p12
KAFKA_KEYSTORE_PASSWORD=your_keystore_password
KAFKA_KEYSTORE_TYPE=PKCS12
KAFKA_TRUSTSTORE_LOCATION=certs/client.truststore.jks
KAFKA_TRUSTSTORE_PASSWORD=your_truststore_password
KAFKA_TRUSTSTORE_TYPE=JKS
KAFKA_KEY_PASSWORD=your_key_password
```

## Configuration Flow
```
.env file → application.yaml → Spring Boot → Kafka Config Classes
```

1. **Environment variables** are defined in `.env` file
2. **application.yaml** references these variables using `${VARIABLE_NAME}`
3. **Spring Boot** loads and resolves the variables
4. **Kafka Config classes** use `@Value("${spring.kafka.property}")` to access resolved values

## AWS S3
```bash
AWS_ACCESS_KEY_ID=your_aws_access_key
AWS_SECRET_ACCESS_KEY=your_aws_secret_key
AWS_REGION=your_aws_region
AWS_BUCKET_NAME=your_bucket_name
AWS_FOLDER_PREFIX=your_folder_prefix
AWS_AVT_FOLDER_PREFIX=avt
```

## Firebase
```bash
FIREBASE_TYPE=service_account
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_PRIVATE_KEY_ID=your_private_key_id
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\nyour_private_key\n-----END PRIVATE KEY-----"
FIREBASE_CLIENT_EMAIL=your_client_email
FIREBASE_CLIENT_ID=your_client_id
FIREBASE_AUTH_URI=https://accounts.google.com/o/oauth2/auth
FIREBASE_TOKEN_URI=https://oauth2.googleapis.com/token
FIREBASE_AUTH_PROVIDER_X509_CERT_URL=https://www.googleapis.com/oauth2/v1/certs
FIREBASE_CLIENT_X509_CERT_URL=your_client_cert_url
FIREBASE_UNIVERSE_DOMAIN=googleapis.com
```

## Setup Instructions

### Local Development
1. Create `.env` file in project root
2. Add all required environment variables
3. Spring Boot will automatically load from `.env` file

### Production Deployment
Set environment variables in your deployment platform:
- **Render**: Environment Variables section in dashboard
- **Heroku**: Config Vars in settings
- **Docker**: ENV instructions in Dockerfile
- **Kubernetes**: ConfigMap and Secrets

## Security Notes
- ✅ `.env` file is already in `.gitignore`
- ✅ No hardcoded secrets in source code
- ✅ Environment variables are loaded at runtime
- ❌ Never commit `.env` file to version control
- ❌ Never share secrets in plain text
