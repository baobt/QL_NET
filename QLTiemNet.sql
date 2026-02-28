USE master
GO

Drop database QLTiemNet
-- Tạo cơ sở dữ liệu
CREATE DATABASE QLTiemNet
ON
(
    NAME = QLTiemNet_dat,
    FILENAME = 'D:\UIT\KY_2\IE303_Java\ASM\DB\QLTiemNet_dat.mdf',
    SIZE = 5MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
LOG ON
(
    NAME = QLTiemNet_log,
    FILENAME = 'D:\UIT\KY_2\IE303_Java\ASM\DB\QLTiemNet_log.ldf',
    SIZE = 5MB,
    MAXSIZE = 50MB,
    FILEGROWTH = 5MB
)
GO

USE QLTiemNet
GO

-- 1. ComputerGroup: nhóm máy và giá giờ
CREATE TABLE ComputerGroup (
    group_id            INT IDENTITY(1,1) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    description         NVARCHAR(MAX),
    hourly_rate         DECIMAL(10,2) NOT NULL,
    status              VARCHAR(50) NOT NULL    -- ví dụ: 'active', 'inactive'
);

-- 2. Computer: danh sách máy, tham chiếu nhóm
CREATE TABLE Computer (
    computer_id         INT IDENTITY(1,1) PRIMARY KEY,
    group_id            INT NOT NULL,
    name                VARCHAR(100) NOT NULL,
    status              VARCHAR(50) NOT NULL, -- ví dụ: 'free', 'in_use', 'maintenance'
    specifications      NVARCHAR(MAX),
    ip_address          VARCHAR(15),
    position            INT,                    -- ví dụ vị trí số bàn
    FOREIGN KEY (group_id) REFERENCES ComputerGroup(group_id)
);

-- 3. Customer: thông tin khách hàng (tên đăng nhập, mật khẩu, SĐT)
CREATE TABLE Customer (
    customer_id         INT IDENTITY(1,1) PRIMARY KEY,
	fullname			VARCHAR(100) NOT NULL,
    username            VARCHAR(50) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    phone               VARCHAR(15) UNIQUE,
    status              VARCHAR(50) DEFAULT 'active',  -- ví dụ: active/inactive
	balance             DECIMAL(10,2) NOT NULL DEFAULT 0
);

-- 4. Employee: quản lý nhân viên
CREATE TABLE Employee (
    employee_id         INT IDENTITY(1,1) PRIMARY KEY,
    username            VARCHAR(50) NOT NULL UNIQUE,
    password_hash       VARCHAR(255) NOT NULL,
    full_name           VARCHAR(100) NOT NULL,
	phone				VARCHAR(20) NOT NULL,
    role                VARCHAR(50) NOT NULL,    -- ví dụ: 'admin', 'reception', 'technician'
    status              VARCHAR(50) DEFAULT 'active'
);

-- 5. Service: danh mục dịch vụ ngoài máy (nước uống, snack...)
CREATE TABLE [Service] (
    service_id          INT IDENTITY(1,1) PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    description         NVARCHAR(MAX),
    price               DECIMAL(10,2) NOT NULL,
    status              VARCHAR(50) DEFAULT 'available'
);

-- 6. ComputerUsage: phiên sử dụng máy
CREATE TABLE ComputerUsage (
    usage_id            INT IDENTITY(1,1) PRIMARY KEY,
    computer_id         INT NOT NULL,
    customer_id         INT,
    employee_id         INT,                    -- nhân viên tạo phiên
    start_time          DATETIME NOT NULL,
    end_time            DATETIME,
    hourly_rate         DECIMAL(10,2) NOT NULL, -- Thêm giá giờ tại thời điểm sử dụng
    total_amount        DECIMAL(10,2),          -- Có thể để NULL và tính toán sau
    FOREIGN KEY (computer_id) REFERENCES Computer(computer_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    CHECK (end_time IS NULL OR end_time > start_time)
);

-- 7. ServiceOrder: đơn gọi thêm dịch vụ trong phiên máy
CREATE TABLE ServiceOrder (
    order_id            INT IDENTITY(1,1) PRIMARY KEY,
    usage_id            INT NOT NULL,
    order_time          DATETIME NOT NULL,
    total_amount        DECIMAL(10,2),          -- Có thể để NULL và tính toán sau
    payment_status      VARCHAR(20) DEFAULT 'unpaid',
    FOREIGN KEY (usage_id) REFERENCES ComputerUsage(usage_id)
);

-- 8. ServiceOrderDetail: chi tiết từng dịch vụ trong đơn
CREATE TABLE ServiceOrderDetail (
    detail_id           INT IDENTITY(1,1) PRIMARY KEY,
    order_id            INT NOT NULL,
    service_id          INT NOT NULL,
    quantity            INT NOT NULL DEFAULT 1,
    unit_price          DECIMAL(10,2) NOT NULL,
    amount              DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES ServiceOrder(order_id),
    FOREIGN KEY (service_id) REFERENCES Service(service_id)
);

-- 9. [Transaction]: lịch sử thanh toán, nạp tiền, thanh toán dịch vụ
CREATE TABLE [Transaction] (
    transaction_id      INT IDENTITY(1,1) PRIMARY KEY,
    customer_id         INT NOT NULL,
    usage_id            INT,                    -- có thể NULL nếu không liên quan phiên máy
    order_id            INT,                    -- có thể NULL nếu không liên quan đơn dịch vụ
    type                VARCHAR(50) NOT NULL,    -- 'topup', 'usage_payment', 'service_payment'
    amount              DECIMAL(10,2) NOT NULL,
    transaction_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    method              VARCHAR(50),            -- 'cash', 'card', 'momo'…
    status              VARCHAR(20) DEFAULT 'completed',
    reference_id        VARCHAR(100),            -- mã hóa đơn hoặc giao dịch
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (usage_id) REFERENCES ComputerUsage(usage_id),
    FOREIGN KEY (order_id) REFERENCES ServiceOrder(order_id)
);

-- 10. ChatSession: phiên chat giữa khách và nhân viên
CREATE TABLE ChatSession (
    session_id          INT IDENTITY(1,1) PRIMARY KEY,
    customer_id         INT NOT NULL,
    employee_id         INT NULL,
    computer_id         INT NOT NULL,
    computer_name       VARCHAR(50),
    started_at          DATETIME DEFAULT CURRENT_TIMESTAMP,
    ended_at            DATETIME NULL,
    status              VARCHAR(20) DEFAULT 'waiting',
    has_unread          BIT DEFAULT 1,
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id),
    FOREIGN KEY (computer_id) REFERENCES Computer(computer_id)
);

-- 11a. CustomerMessage: tin nhắn từ khách hàng
CREATE TABLE CustomerMessage (
    message_id          INT IDENTITY(1,1) PRIMARY KEY,
    session_id          INT NOT NULL,
    customer_id         INT NOT NULL,
    sent_at             DATETIME DEFAULT CURRENT_TIMESTAMP,
    content             NVARCHAR(MAX) NOT NULL,
    is_read             BIT DEFAULT 0,
    FOREIGN KEY (session_id) REFERENCES ChatSession(session_id),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
);

-- 11b. EmployeeMessage: tin nhắn từ nhân viên
CREATE TABLE EmployeeMessage (
    message_id          INT IDENTITY(1,1) PRIMARY KEY,
    session_id          INT NOT NULL,
    employee_id         INT NOT NULL,
    sent_at             DATETIME DEFAULT CURRENT_TIMESTAMP,
    content             NVARCHAR(MAX) NOT NULL,
    is_read             BIT DEFAULT 0,
    FOREIGN KEY (session_id) REFERENCES ChatSession(session_id),
    FOREIGN KEY (employee_id) REFERENCES Employee(employee_id)
);

-- 12. Chỉ mục tối ưu truy vấn
CREATE INDEX idx_computer_usage_computer ON ComputerUsage(computer_id, end_time);
CREATE INDEX idx_transaction_time ON [Transaction](transaction_time);
CREATE INDEX idx_service_order_usage ON ServiceOrder(usage_id);
CREATE INDEX idx_customer_message_session ON CustomerMessage(session_id);
CREATE INDEX idx_employee_message_session ON EmployeeMessage(session_id);
GO


ALTER TABLE Service ADD [image] NVARCHAR(100),
						quantity INT;

ALTER TABLE ComputerUsage ADD total_time INT;
--ALTER TABLE ComputerUsage ALTER COLUMN customer_id INT NULL;

--ALTER TABLE Customer ADD balance DECIMAL(10,2) NOT NULL DEFAULT 0;


--==========================================DATA======================================
INSERT INTO ComputerGroup (name, description, hourly_rate, status)
VALUES 
    ('Standard', 'Máy cấu hình cơ bản, phù hợp chơi game nhẹ', 10000, 'active'),
    ('VIP', 'Máy cấu hình cao, màn hình lớn', 15000, 'active'),
    ('Gaming Pro', 'Máy chuyên dụng cho game thủ, RTX 3080', 20000, 'active');
GO

-- Thêm dữ liệu cho loại Standard (group_id = 1)
INSERT INTO Computer (group_id, name, status, specifications, ip_address, position)
VALUES 
(1, 'Máy 1', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.1', 1),
(1, 'Máy 2', 'active', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.2', 2),
(1, 'Máy 3', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.3', 3),
(1, 'Máy 4', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.4', 4),
(1, 'Máy 5', 'maintenance', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.5', 5),
(1, 'Máy 6', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.6', 6),
(1, 'Máy 7', 'active', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.7', 7),
(1, 'Máy 8', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.8', 8),
(1, 'Máy 9', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.9', 9),
(1, 'Máy 10', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.10', 10),
(1, 'Máy 11', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.11', 11),
(1, 'Máy 12', 'active', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.12', 12),
(1, 'Máy 13', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.13', 13),
(1, 'Máy 14', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.14', 14),
(1, 'Máy 15', 'maintenance', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.15', 15),
(1, 'Máy 16', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.16', 16),
(1, 'Máy 17', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.17', 17),
(1, 'Máy 18', 'active', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.18', 18),
(1, 'Máy 19', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.19', 19),
(1, 'Máy 20', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.20', 20),
(1, 'Máy 21', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.21', 21),
(1, 'Máy 22', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.22', 22),
(1, 'Máy 23', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.23', 23),
(1, 'Máy 24', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.24', 24),
(1, 'Máy 25', 'active', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.25', 25),
(1, 'Máy 26', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.26', 26),
(1, 'Máy 27', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.27', 27),
(1, 'Máy 28', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.28', 28),
(1, 'Máy 29', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.29', 29),
(1, 'Máy 30', 'inactive', 'Intel i3, 8GB RAM, GTX 1050', '192.168.1.30', 30);

-- Thêm dữ liệu cho loại VIP (group_id = 2)
INSERT INTO Computer (group_id, name, status, specifications, ip_address, position)
VALUES 
(2, 'Máy VIP 1', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.1', 1),
(2, 'Máy VIP 2', 'active', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.2', 2),
(2, 'Máy VIP 3', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.3', 3),
(2, 'Máy VIP 4', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.4', 4),
(2, 'Máy VIP 5', 'maintenance', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.5', 5),
(2, 'Máy VIP 6', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.6', 6),
(2, 'Máy VIP 7', 'active', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.7', 7),
(2, 'Máy VIP 8', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.8', 8),
(2, 'Máy VIP 9', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.9', 9),
(2, 'Máy VIP 10', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.10', 10),
(2, 'Máy VIP 11', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.11', 11),
(2, 'Máy VIP 12', 'active', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.12', 12),
(2, 'Máy VIP 13', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.13', 13),
(2, 'Máy VIP 14', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.14', 14),
(2, 'Máy VIP 15', 'maintenance', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.15', 15),
(2, 'Máy VIP 16', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.16', 16),
(2, 'Máy VIP 17', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.17', 17),
(2, 'Máy VIP 18', 'active', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.18', 18),
(2, 'Máy VIP 19', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.19', 19),
(2, 'Máy VIP 20', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.20', 20),
(2, 'Máy VIP 21', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.21', 21),
(2, 'Máy VIP 22', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.22', 22),
(2, 'Máy VIP 23', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.23', 23),
(2, 'Máy VIP 24', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.24', 24),
(2, 'Máy VIP 25', 'active', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.25', 25),
(2, 'Máy VIP 26', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.26', 26),
(2, 'Máy VIP 27', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.27', 27),
(2, 'Máy VIP 28', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.28', 28),
(2, 'Máy VIP 29', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.29', 29),
(2, 'Máy VIP 30', 'inactive', 'Intel i7, 16GB RAM, GTX 1660Ti', '192.168.2.30', 30);

-- Thêm dữ liệu cho loại Gaming Pro (group_id = 3)
INSERT INTO Computer (group_id, name, status, specifications, ip_address, position)
VALUES 
(3, 'Máy GPro 1', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.1', 1),
(3, 'Máy GPro 2', 'active', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.2', 2),
(3, 'Máy GPro 3', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.3', 3),
(3, 'Máy GPro 4', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.4', 4),
(3, 'Máy GPro 5', 'maintenance', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.5', 5),
(3, 'Máy GPro 6', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.6', 6),
(3, 'Máy GPro 7', 'active', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.7', 7),
(3, 'Máy GPro 8', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.8', 8),
(3, 'Máy GPro 9', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.9', 9),
(3, 'Máy GPro 10', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.10', 10),
(3, 'Máy GPro 11', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.11', 11),
(3, 'Máy GPro 12', 'active', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.12', 12),
(3, 'Máy GPro 13', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.13', 13),
(3, 'Máy GPro 14', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.14', 14),
(3, 'Máy GPro 15', 'maintenance', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.15', 15),
(3, 'Máy GPro 16', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.16', 16),
(3, 'Máy GPro 17', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.17', 17),
(3, 'Máy GPro 18', 'active', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.18', 18),
(3, 'Máy GPro 19', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.19', 19),
(3, 'Máy GPro 20', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.20', 20),
(3, 'Máy GPro 21', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.21', 21),
(3, 'Máy GPro 22', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.22', 22),
(3, 'Máy GPro 23', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.23', 23),
(3, 'Máy GPro 24', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.24', 24),
(3, 'Máy GPro 25', 'active', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.25', 25),
(3, 'Máy GPro 26', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.26', 26),
(3, 'Máy GPro 27', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.27', 27),
(3, 'Máy GPro 28', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.28', 28),
(3, 'Máy GPro 29', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.29', 29),
(3, 'Máy GPro 30', 'inactive', 'Intel i9, 32GB RAM, RTX 3080', '192.168.3.30', 30);
GO

INSERT INTO Customer (fullname, username, password_hash, phone, status)
VALUES 
    ('Lê Hoàng','khach1', 'hashed_password_1', '0901234567', 'active'),
    ('Đỗ Tấn Ngọc', 'khach2', 'hashed_password_2', '0912345678', 'active'),
    ('Nguyên Văn Bê', 'khach3', 'hashed_password_3', '0923456789', 'inactive');

INSERT INTO Employee (username, password_hash, full_name, phone, role, status) VALUES
('johndoe', '123', 'John Doe', '0123456789', 'admin', 'active'),
('janedoe', '234', 'Jane Doe', '0987654321', 'staff', 'active'),
('bobsmith', '345', 'Bob Smith', '0912345678', 'staff', 'inactive');


-- Thêm dữ liệu cho bảng Service
INSERT INTO [Service] (name, description, price, status, [image], quantity)
VALUES 
    ('Coca Cola', 'Nước ngọt Coca Cola 330ml', 15000, 'available', 'coca.jpg', 50),
    ('Pepsi', 'Nước ngọt Pepsi 330ml', 15000, 'available', 'pepsi.jpg', 30),
    ('Mì tôm Hảo Hảo', 'Mì tôm chua cay', 8000, 'available', 'minom.jpg', 25);
   -- ('Bánh mì thịt nướng', 'Bánh mì thịt nướng + pate', 25000, 'available', 'banhmi.jpg', 10),
  --  ('Trà sữa trân châu', 'Trà sữa trân châu đường đen', 35000, 'available', 'trasua.jpg', 15);

-- Thêm dữ liệu cho bảng ComputerUsage
INSERT INTO ComputerUsage (computer_id, customer_id, employee_id, start_time, end_time, hourly_rate, total_amount, total_time)
VALUES 
    (2, 1, 1, '2024-05-20 14:00:00', '2024-05-20 16:30:00', 10000, 25000, 150),
    (32, 2, 2, '2024-05-20 15:30:00', '2024-05-20 18:00:00', 15000, 37500, 150),
    (62, 1, 1, '2024-05-21 10:00:00', '2024-05-21 13:00:00', 20000, 60000, 180);
 --   (7, 3, 2, '2024-05-21 16:00:00', '2024-05-21 19:30:00', 10000, 35000, 210),
--    (37, 2, 1, '2024-05-22 09:00:00', NULL, 15000, NULL, NULL); -- Phiên đang sử dụng

-- Thêm dữ liệu cho bảng ServiceOrder
INSERT INTO ServiceOrder (usage_id, order_time, total_amount, payment_status)
VALUES 
    (1, '2024-05-20 15:00:00', 30000, 'paid'),
    (2, '2024-05-20 16:30:00', 50000, 'paid'),
    (3, '2024-05-21 11:30:00', 15000, 'paid');
 --   (4, '2024-05-21 17:00:00', 43000, 'unpaid'),
  --  (5, '2024-05-22 10:30:00', 25000, 'unpaid');

-- Thêm dữ liệu cho bảng ServiceOrderDetail
INSERT INTO ServiceOrderDetail (order_id, service_id, quantity, unit_price, amount)
VALUES 
    (1, 1, 2, 15000, 30000), -- 2 Coca Cola
    (2, 2, 1, 25000, 25000), -- 1 Bánh mì thịt nướng
    (2, 3, 1, 35000, 35000), -- 1 Trà sữa trân châu (tổng 60000 nhưng trong order chỉ 50000 - có thể có discount)
    (3, 2, 1, 15000, 15000); -- 1 Pepsi
   -- (4, 3, 2, 8000, 16000),  -- 2 Mì tôm
   -- (4, 1, 1, 15000, 15000), -- 1 Coca Cola
    --(4, 3, 1, 8000, 8000),   -- 1 Mì tôm thêm (tổng order 39000 nhưng ghi 43000)
   -- (5, 4, 1, 25000, 25000); -- 1 Bánh mì thịt nướng

-- Thêm dữ liệu cho bảng Transaction
INSERT INTO [Transaction] (customer_id, usage_id, order_id, type, amount, transaction_time, method, status, reference_id)
VALUES 
    (1, NULL, NULL, 'topup', 100000, '2024-05-20 13:30:00', 'cash', 'completed', 'TOP001'),
    (1, 1, NULL, 'usage_payment', 25000, '2024-05-20 16:30:00', 'balance', 'completed', 'USE001'),
    (1, 1, 1, 'service_payment', 30000, '2024-05-20 16:30:00', 'balance', 'completed', 'SER001'),
    (2, 2, 2, 'service_payment', 50000, '2024-05-20 18:00:00', 'cash', 'completed', 'SER002'),
    (3, NULL, NULL, 'topup', 50000, '2024-05-21 15:30:00', 'momo', 'completed', 'TOP002');



--===================================TruyVan================================
