# QuanLyTiemNet - Hệ thống Quản lý Tiệm Net

## 📋 Mô tả dự án

QuanLyTiemNet là một hệ thống quản lý tiệm internet (cyber cafe) được phát triển bằng Java Swing. Hệ thống cung cấp các chức năng quản lý toàn diện cho việc vận hành một tiệm net, bao gồm quản lý máy tính, khách hàng, nhân viên, dịch vụ và thanh toán.

## ✨ Tính năng chính

### 🖥️ Quản lý máy tính
- Phân loại máy theo nhóm (Standard, VIP, Gaming Pro)
- Theo dõi trạng thái máy (trống, đang sử dụng, bảo trì)
- Quản lý thông số kỹ thuật và vị trí máy
- Theo dõi hoạt động sử dụng máy theo thời gian thực

### 👥 Quản lý khách hàng
- Đăng ký và quản lý thông tin khách hàng
- Quản lý số dư tài khoản
- Lịch sử sử dụng và giao dịch
- Hỗ trợ đăng nhập cho khách hàng

### 👨‍💼 Quản lý nhân viên
- Phân quyền admin và nhân viên
- Quản lý thông tin nhân viên
- Xác thực đăng nhập với mã hóa mật khẩu (bcrypt)

### 🛒 Đặt hàng dịch vụ
- Menu dịch vụ (đồ uống, đồ ăn nhẹ)
- Đặt hàng trong phiên sử dụng máy
- Quản lý tồn kho
- Tính toán hóa đơn tự động

### 💬 Hệ thống chat
- Chat thời gian thực giữa khách hàng và nhân viên
- Phiên chat được lưu trữ trong cơ sở dữ liệu
- Thông báo tin nhắn chưa đọc

### 💰 Quản lý thanh toán
- Thanh toán theo giờ sử dụng máy
- Thanh toán dịch vụ đặt hàng
- Nạp tiền vào tài khoản
- Lịch sử giao dịch chi tiết

### 📊 Dashboard và báo cáo
- Giám sát hoạt động máy tính theo thời gian thực
- Thống kê doanh thu
- Báo cáo sử dụng máy
- Giao diện trực quan với biểu đồ

## 🛠️ Công nghệ sử dụng

### Backend
- **Java 8** - Ngôn ngữ lập trình chính
- **Maven** - Quản lý dependencies và build
- **SQL Server** - Cơ sở dữ liệu
- **JDBC** - Kết nối cơ sở dữ liệu

### Frontend
- **Java Swing** - Framework GUI
- **FlatLaf** - Theme hiện đại cho Swing
- **AbsoluteLayout** - Layout manager

### Thư viện chính
- `jbcrypt-0.4` - Mã hóa mật khẩu
- `jcalendar-1.4` - Component chọn ngày
- `jfreechart-1.0.19` - Vẽ biểu đồ thống kê
- `jcommon-1.0.23` - Thư viện hỗ trợ cho JFreeChart
- `mssql-jdbc-8.2.2.jre8` - Driver SQL Server

## 🏗️ Kiến trúc hệ thống

```
QuanLyTiemNet/
├── src/main/java/QLTN/
│   ├── Dao/           # Data Access Objects
│   ├── Entity/        # Entity classes
│   ├── UI/           # Swing UI components
│   └── Utils/        # Utility classes
├── src/main/resources/# Resources (icons, images)
├── src/test/         # Unit tests
├── lib/             # External libraries
├── pom.xml          # Maven configuration
└── QLTiemNet.sql    # Database schema
```

### Cấu trúc Database

#### Các bảng chính:
- `ComputerGroup` - Nhóm máy tính
- `Computer` - Máy tính cụ thể
- `Customer` - Khách hàng
- `Employee` - Nhân viên
- `Service` - Dịch vụ (đồ uống, đồ ăn)
- `ComputerUsage` - Phiên sử dụng máy
- `ServiceOrder` & `ServiceOrderDetail` - Đơn hàng dịch vụ
- `Transaction` - Giao dịch thanh toán
- `ChatSession`, `CustomerMessage`, `EmployeeMessage` - Hệ thống chat

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống
- Java 8 hoặc cao hơn
- SQL Server 2016 trở lên
- Maven 3.x
- Windows/Linux/MacOS

### Các bước cài đặt

1. **Clone repository:**
   ```bash
   git clone <repository-url>
   cd QuanLyTiemNet
   ```

2. **Cài đặt dependencies:**
   ```bash
   mvn clean install
   ```

3. **Thiết lập cơ sở dữ liệu:**
   - Tạo database SQL Server
   - Chạy script `QLTiemNet.sql` để tạo bảng và dữ liệu mẫu
   - Cập nhật thông tin kết nối database trong code (nếu cần)

4. **Chạy ứng dụng:**
   ```bash
   mvn exec:java
   ```
   hoặc
   ```bash
   java -jar target/QuanLyTiemNet.jar
   ```

## 🔐 Tài khoản mặc định

### Admin
- Username: `johndoe`
- Password: `123`
- Role: admin

### Nhân viên
- Username: `janedoe`
- Password: `234`
- Role: staff

## 📱 Giao diện người dùng

### Giao diện chính
- **Menu bên trái:** Điều hướng giữa các chức năng
- **Header:** Thông tin người dùng hiện tại
- **Content area:** Hiển thị nội dung chức năng được chọn

### Các màn hình chính
1. **Hoạt động máy:** Giám sát trạng thái máy tính theo thời gian thực
2. **Quản lý máy:** Thêm/sửa/xóa máy tính và nhóm máy
3. **Quản lý khách hàng:** Quản lý thông tin và số dư khách hàng
4. **Quản lý menu:** Quản lý dịch vụ và đồ uống
5. **Nhân viên:** Quản lý nhân viên (chỉ admin)

## 🔧 Cấu hình

### Kết nối Database
Thông tin kết nối database được cấu hình trong các DAO classes. Đảm bảo:
- SQL Server đang chạy
- Database `QLTiemNet` đã được tạo
- Thông tin kết nối chính xác

### File cấu hình
- `pom.xml` - Dependencies và build configuration
- Database script trong `QLTiemNet.sql`

## 📈 Tính năng nâng cao

- **Authentication & Authorization:** Phân quyền admin/nhân viên
- **Real-time monitoring:** Theo dõi hoạt động máy tính
- **Chat system:** Hỗ trợ khách hàng
- **Transaction management:** Quản lý thanh toán toàn diện
- **Reporting:** Thống kê với biểu đồ
- **Responsive UI:** Giao diện thân thiện với người dùng

## 🐛 Xử lý lỗi

### Lỗi thường gặp
1. **Không kết nối được database:** Kiểm tra SQL Server đang chạy và thông tin kết nối
2. **Không load được UI:** Đảm bảo tất cả dependencies đã được cài đặt
3. **Lỗi phân quyền:** Chỉ admin mới truy cập được một số chức năng

### Debug
- Kiểm tra console output cho error messages
- Verify database connections
- Check file permissions cho resources

## 🤝 Đóng góp

1. Fork project
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📝 License

Dự án này được phát triển cho mục đích học tập và thương mại.

