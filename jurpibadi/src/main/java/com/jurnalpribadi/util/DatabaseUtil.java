package com.jurnalpribadi.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String DB_NAME = "jurnal_pribadi.db";
    // Databasenya bakal ngikutin dari folder target
    private static final String DB_PATH = DB_NAME; 
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    
    static {
        try {
            
            Class.forName("org.sqlite.JDBC");
            initDatabase();
            
            // Simpan alamat database untuk debugging
            File dbFile = new File(DB_PATH);
            System.out.println("SQLite database configured at relative path: " + DB_PATH);
            System.out.println("Attempting to use/create database at absolute path: " + dbFile.getAbsolutePath());
            if (!dbFile.exists() && !dbFile.getParentFile().exists()) {
                 System.out.println("Parent directory for DB does not exist: " + dbFile.getParentFile().getAbsolutePath());
            }

        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error during DatabaseUtil static initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    private static void initDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // buat tabel users
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT UNIQUE NOT NULL," +
                    "email TEXT UNIQUE NOT NULL," +
                    "password_hash TEXT NOT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");
            
            // buat tabel journal_entries
            stmt.execute("CREATE TABLE IF NOT EXISTS journal_entries (" +
                    "entry_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "entry_date DATE NOT NULL," +
                    "content TEXT NOT NULL," +
                    "mood TEXT," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME," +
                    "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE)");
        }
    }
    
    public static void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    // System.err.println("Error closing resource: " + e.getMessage()); // Reduced verbosity
                }
            }
        }
    }
} 