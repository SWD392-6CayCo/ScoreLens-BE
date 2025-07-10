# Google Login API Test

## API Endpoint
```
POST /v2/auth/login-google
```

## Request Body
```json
{
    "email": "user@gmail.com",
    "name": "User Name",
    "picture": "https://lh3.googleusercontent.com/a/profile-picture-url"
}
```

## Response
```json
{
    "status": 1000,
    "message": "Login with Google successfully!!",
    "data": {
        "authenticated": true,
        "accessToken": "jwt-access-token",
        "refreshToken": "jwt-refresh-token",
        "user": {
            "customerID": "C0000001",
            "fullName": "User Name",
            "email": "user@gmail.com",
            "phoneNumber": null,
            "dob": null,
            "address": null,
            "createAt": "10-07-2025",
            "updateAt": null,
            "type": null,
            "status": "active",
            "imageUrl": "https://lh3.googleusercontent.com/a/profile-picture-url"
        },
        "userType": "CUSTOMER"
    }
}
```

## Test với cURL
```bash
curl -X POST "http://localhost:8080/v2/auth/login-google" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@gmail.com",
    "name": "Test User",
    "picture": "https://lh3.googleusercontent.com/a/test-picture"
  }' \
  -c cookies.txt
```

## Tính năng
1. **Tự động tạo tài khoản**: Nếu email chưa tồn tại, hệ thống sẽ tự động tạo customer mới
2. **Lưu thông tin Google**: Lưu tên và ảnh đại diện từ Google
3. **JWT Token**: Tạo access token và refresh token
4. **Cookie Support**: Lưu tokens vào httpOnly cookies
5. **Customer Type**: Tự động set userType là CUSTOMER

## Security
- Endpoint được thêm vào PUBLIC_ENDPOINTS trong SecurityConfig
- Không cần authentication để gọi API này
- Tokens được lưu trong httpOnly cookies để bảo mật
