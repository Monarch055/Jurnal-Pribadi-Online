package com.jurnalpribadi.dao;

import java.sql.SQLException;
import java.util.Optional;

import com.jurnalpribadi.model.User;

public interface UserDAO {
    int create(User user) throws SQLException;
    Optional<User> findById(int id) throws SQLException;
    Optional<User> findByUsername(String username) throws SQLException;
    Optional<User> findByEmail(String email) throws SQLException;
    boolean update(User user) throws SQLException;
    boolean delete(int id) throws SQLException;
} 