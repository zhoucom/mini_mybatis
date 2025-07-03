-- Mini MyBatis Gradle示例 - 初始化数据

-- 插入商品数据
INSERT INTO products (name, price, category, description, stock_quantity) VALUES 
('MacBook Pro 16寸', 16999.00, '电子产品', 'Apple M2 Pro芯片，16GB内存，512GB存储', 10),
('iPhone 14 Pro', 7999.00, '电子产品', '6.1英寸超视网膜XDR显示屏，256GB存储', 25),
('AirPods Pro 2代', 1899.00, '电子产品', '主动降噪，空间音频，无线充电盒', 50),
('Herman Miller座椅', 8999.00, '办公家具', '人体工学设计，12年质保', 5),
('宜家书桌', 599.00, '办公家具', '简约现代设计，环保材料', 20),
('罗技MX Master 3', 699.00, '电子配件', '高精度传感器，多设备连接', 30),
('戴尔4K显示器', 2999.00, '电子产品', '27英寸4K IPS显示屏，Type-C接口', 15),
('Kindle Oasis', 2399.00, '电子产品', '7英寸电子墨水屏，防水设计', 40);

-- 插入用户数据  
INSERT INTO users (username, email, age, status) VALUES
('张三', 'zhangsan@example.com', 25, 'ACTIVE'),
('李四', 'lisi@example.com', 30, 'ACTIVE'), 
('王五', 'wangwu@example.com', 28, 'ACTIVE'),
('赵六', 'zhaoliu@example.com', 35, 'ACTIVE'),
('钱七', 'qianqi@example.com', 22, 'INACTIVE'),
('孙八', 'sunba@example.com', 29, 'ACTIVE'),
('周九', 'zhoujiu@example.com', 31, 'ACTIVE');

-- 插入订单数据
INSERT INTO orders (user_id, product_id, quantity, total_amount, order_status) VALUES
(1, 1, 1, 16999.00, 'COMPLETED'),
(1, 3, 2, 3798.00, 'COMPLETED'), 
(2, 2, 1, 7999.00, 'PENDING'),
(2, 6, 1, 699.00, 'COMPLETED'),
(3, 5, 2, 1198.00, 'COMPLETED'),
(4, 4, 1, 8999.00, 'PENDING'),
(6, 7, 1, 2999.00, 'COMPLETED'),
(7, 8, 3, 7197.00, 'PENDING'); 