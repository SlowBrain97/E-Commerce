# Database Migration với Flyway

## Tổng quan

Dự án sử dụng **Flyway** để quản lý database migrations và tự động khởi tạo dữ liệu mẫu khi chạy ứng dụng lần đầu tiên.

## Cấu trúc Migration

```
src/main/resources/db/migration/
└── V2__Initialize_admin_and_categories.sql
```

## Nội dung Migration

Migration script `V2__Initialize_admin_and_categories.sql` sẽ:

### 1. Tạo Admin User
- **Username**: `admin`
- **Email**: `admin@ecommerce.com`
- **Password**: `admin123` (đã được hash với BCrypt)
- **Role**: `ADMIN`
- **Trạng thái**: Đã xác thực và kích hoạt

### 2. Tạo Categories Cơ Bản
- **Electronics** (Featured)
  - Smartphones
  - Laptops
  - Tablets
  - Audio
  - Cameras
- **Clothing** (Featured)
  - Men's Clothing
  - Women's Clothing
  - Shoes
  - Accessories
- **Books**
- **Home & Garden**
- **Sports & Outdoors**
- **Health & Beauty**
- **Toys & Games**
- **Automotive**

## Cách hoạt động

1. **Lần đầu chạy**: Flyway sẽ tự động chạy migration và tạo admin user + categories
2. **Các lần chạy sau**: Flyway sẽ bỏ qua migration đã chạy (do có checksum)
3. **Development**: Có thể clean và migrate lại bằng cách xóa bảng `flyway_schema_history`

## Bảo mật

⚠️ **Quan trọng**: Trong production, hãy thay đổi:
- Thông tin đăng nhập admin
- Mật khẩu mặc định
- Các thông tin nhạy cảm khác

## Cấu hình Flyway

Flyway đã được cấu hình trong `application.yml`:

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    validate-on-migrate: true
    clean-disabled: true
    locations: classpath:db/migration
    baseline-version: 1
    baseline-description: "Initial schema"

  jpa:
    hibernate:
      ddl-auto: validate # Flyway quản lý schema thay vì Hibernate
```

## Sử dụng

### 1. Chuẩn bị Database
```sql
CREATE DATABASE ecommerce_db;
CREATE USER ecommerce_user WITH PASSWORD 'ecommerce_password';
GRANT ALL PRIVILEGES ON DATABASE ecommerce_db TO ecommerce_user;
```

### 2. Chạy ứng dụng
```bash
./mvnw spring-boot:run
# hoặc
./gradlew bootRun
```

### 3. Đăng nhập Admin
- **URL**: `http://localhost:8080/api/auth/login`
- **Email**: `admin@ecommerce.com`
- **Password**: `admin123`

## Troubleshooting

### Nếu gặp lỗi "Table already exists"
- Xóa database và tạo lại
- Hoặc chạy: `DROP TABLE IF EXISTS flyway_schema_history CASCADE;`

### Nếu muốn reset dữ liệu
```sql
-- Xóa toàn bộ dữ liệu và migration history
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO ecommerce_user;
```

### Kiểm tra migrations đã chạy
```sql
SELECT * FROM flyway_schema_history ORDER BY version;
```

## Lưu ý

- Migration chỉ chạy một lần duy nhất
- Các lần chạy sau sẽ được skip tự động
- Trong môi trường production, hãy backup dữ liệu trước khi chạy migrations mới
