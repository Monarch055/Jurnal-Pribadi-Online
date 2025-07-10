package com.jurnalpribadi.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.jurnalpribadi.model.JournalEntry;

public interface JournalEntryDAO {
    int create(JournalEntry entry) throws SQLException;
    Optional<JournalEntry> findById(int id) throws SQLException;
    List<JournalEntry> findByUserId(int userId) throws SQLException;
    List<JournalEntry> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
        throws SQLException;
    List<JournalEntry> findByUserIdAndKeyword(int userId, String keyword) throws SQLException;
    boolean update(JournalEntry entry) throws SQLException;
    boolean delete(int id) throws SQLException;
} 