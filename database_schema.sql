-- Hotel Management System Database Schema
-- MySQL 8.0+

-- Create database
CREATE DATABASE IF NOT EXISTS hotel_management;
USE hotel_management;

-- Guests table
CREATE TABLE IF NOT EXISTS guests (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20) NOT NULL,
    id_number VARCHAR(50) UNIQUE NOT NULL,
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    room_number VARCHAR(10) PRIMARY KEY,
    type ENUM('SINGLE', 'DOUBLE', 'SUITE', 'DELUXE') NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    price_per_night DECIMAL(10, 2) NOT NULL,
    description TEXT,
    max_occupancy INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    room_number VARCHAR(10) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED') DEFAULT 'PENDING',
    total_price DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE,
    FOREIGN KEY (room_number) REFERENCES rooms(room_number) ON DELETE CASCADE,
    INDEX idx_guest (guest_id),
    INDEX idx_room (room_number),
    INDEX idx_dates (check_in_date, check_out_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table
CREATE TABLE IF NOT EXISTS payments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'ONLINE') NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'COMPLETED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    INDEX idx_reservation (reservation_id),
    INDEX idx_status (status),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Services table
CREATE TABLE IF NOT EXISTS services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category ENUM('ROOM_SERVICE', 'LAUNDRY', 'SPA', 'TRANSPORT', 'MINIBAR', 'HOUSEKEEPING', 'OTHER') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reservation Services junction table
CREATE TABLE IF NOT EXISTS reservation_services (
    id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT DEFAULT 1,
    date_requested TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    total_price DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    INDEX idx_reservation (reservation_id),
    INDEX idx_service (service_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data

-- Sample Rooms
INSERT INTO rooms (room_number, type, status, price_per_night, description, max_occupancy) VALUES
('101', 'SINGLE', 'AVAILABLE', 75.00, 'Cozy single room with city view', 1),
('102', 'SINGLE', 'AVAILABLE', 75.00, 'Cozy single room with garden view', 1),
('201', 'DOUBLE', 'AVAILABLE', 120.00, 'Spacious double room with king bed', 2),
('202', 'DOUBLE', 'AVAILABLE', 120.00, 'Spacious double room with twin beds', 2),
('301', 'SUITE', 'AVAILABLE', 250.00, 'Luxury suite with living area and kitchenette', 4),
('302', 'SUITE', 'AVAILABLE', 250.00, 'Executive suite with balcony', 4),
('401', 'DELUXE', 'AVAILABLE', 400.00, 'Presidential deluxe suite with panoramic view', 6),
('402', 'DELUXE', 'AVAILABLE', 400.00, 'Royal deluxe suite with jacuzzi', 6);

-- Sample Services
INSERT INTO services (name, description, price, category, is_active) VALUES
('Room Service - Breakfast', 'Continental breakfast delivered to room', 25.00, 'ROOM_SERVICE', TRUE),
('Room Service - Dinner', 'Full dinner menu available', 45.00, 'ROOM_SERVICE', TRUE),
('Laundry Service', 'Professional laundry and dry cleaning', 30.00, 'LAUNDRY', TRUE),
('Spa Massage', '60-minute relaxation massage', 80.00, 'SPA', TRUE),
('Airport Transfer', 'Round trip airport transportation', 50.00, 'TRANSPORT', TRUE),
('Minibar Restock', 'Minibar refreshment and restock', 35.00, 'MINIBAR', TRUE),
('Extra Housekeeping', 'Additional room cleaning service', 20.00, 'HOUSEKEEPING', TRUE);

-- Sample Guest (optional)
INSERT INTO guests (first_name, last_name, email, phone, id_number, address) VALUES
('John', 'Doe', 'john.doe@email.com', '+1234567890', 'ID123456', '123 Main Street, City, Country');
