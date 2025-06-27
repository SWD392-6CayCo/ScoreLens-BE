# Cookie-Based Authentication Setup

## Overview
Ứng dụng đã được cấu hình để hỗ trợ cookie-based authentication thay vì Authorization header. Điều này an toàn hơn vì httpOnly cookies không thể bị truy cập bởi JavaScript, tránh được XSS attacks.

## Client Configuration

### Frontend (React/Vue/Angular)
```javascript
const api = axios.create({
    baseURL: `${import.meta.env.VITE_API_URL}`,
    // withCredentials là chìa khóa để trình duyệt tự động gửi httpOnly cookie
    withCredentials: true,
});
```

### Cookie Names
- **AccessToken**: Chứa JWT access token (expires: 1 hour)
- **RefreshToken**: Chứa JWT refresh token (expires: 7 days)

## API Endpoints

### Authentication V2 (Recommended - with Cookie Support)

#### Login
```
POST /v2/auth/login
Content-Type: application/json

{
    "email": "user@example.com",
    "password": "password123"
}
```

**Response**: Sets httpOnly cookies và trả về user info
```json
{
    "status": 1000,
    "message": "Login successfully!!",
    "data": {
        "authenticated": true,
        "user": { ... },
        "userType": "CUSTOMER"
    }
}
```

#### Logout
```
POST /v2/auth/logout
```
**Note**: Cookies sẽ được tự động gửi, không cần body

#### Refresh Token
```
POST /v2/auth/refresh
```
**Note**: RefreshToken cookie sẽ được tự động gửi

### Authentication V1 (Legacy - Header-based)
Vẫn hoạt động với Authorization header cho backward compatibility.

## How It Works

### 1. CookieJwtAuthenticationFilter
- Tự động extract JWT token từ `AccessToken` cookie
- Thêm vào Authorization header cho Spring Security
- Chỉ hoạt động khi không có Authorization header

### 2. TokenCookieManager
- Set httpOnly cookies khi login
- Clear cookies khi logout
- Cấu hình secure, SameSite=None cho CORS

### 3. Security Configuration
- CORS được cấu hình với `allowCredentials: true`
- Filter được thêm vào security chain

## Testing

### Manual Testing
1. Login qua `/v2/auth/login`
2. Kiểm tra cookies trong browser DevTools
3. Gọi protected APIs mà không cần set Authorization header
4. Logout qua `/v2/auth/logout`

### Automated Testing
```bash
# Run cookie authentication tests
mvn test -Dtest=CookieJwtAuthenticationFilterTest
```

## Security Features

### Cookie Configuration
- **HttpOnly**: true (không thể truy cập qua JavaScript)
- **Secure**: true (chỉ gửi qua HTTPS)
- **SameSite**: None (cho phép cross-site requests)
- **Path**: / (áp dụng cho toàn bộ domain)

### Token Management
- Access token: 1 hour expiry
- Refresh token: 7 days expiry
- Automatic token blacklisting khi logout

## Migration Guide

### From Header-based to Cookie-based
1. Update client code để sử dụng `withCredentials: true`
2. Remove manual Authorization header setting
3. Use `/v2/auth/*` endpoints thay vì `/v1/auth/*`
4. Handle logout properly để clear cookies

### Backward Compatibility
- `/v1/auth/*` endpoints vẫn hoạt động với Authorization header
- Có thể mix cả hai approaches trong cùng application
