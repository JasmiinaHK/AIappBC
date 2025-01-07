-- Create test database if it doesn't exist
CREATE DATABASE IF NOT EXISTS baza_ai_test;

-- Use the test database
USE baza_ai_test;

-- Grant privileges to root user (adjust as needed)
GRANT ALL PRIVILEGES ON baza_ai_test.* TO 'root'@'localhost';
