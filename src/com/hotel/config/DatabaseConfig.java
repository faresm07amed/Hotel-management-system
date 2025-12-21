package com.hotel.config;

public class DatabaseConfig {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/hotel_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    public static String getDbUrl() {
        return DB_URL;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static String getDbDriver() {
        return DB_DRIVER;
    }
}
