package com.jurnalpribadi.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jurnalpribadi.model.JournalEntry;
import com.jurnalpribadi.util.DatabaseUtil;

public class JournalEntryDAOImpl implements JournalEntryDAO {
    
    @Override
    public int create(JournalEntry entry) throws SQLException {
        String sql = "INSERT INTO journal_entries (user_id, entry_date, content, mood, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, entry.getUserId());
            pstmt.setDate(2, Date.valueOf(entry.getEntryDate()));
            pstmt.setString(3, entry.getContent());
            pstmt.setString(4, entry.getMood());
            
            LocalDateTime createdAt = entry.getCreatedAt() != null ? entry.getCreatedAt() : LocalDateTime.now();
            LocalDateTime updatedAt = entry.getUpdatedAt() != null ? entry.getUpdatedAt() : LocalDateTime.now();
            
            pstmt.setTimestamp(5, Timestamp.valueOf(createdAt));
            pstmt.setTimestamp(6, Timestamp.valueOf(updatedAt));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating journal entry failed, no rows affected.");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Creating journal entry failed, no ID obtained.");
            }
        } finally {
            DatabaseUtil.closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public Optional<JournalEntry> findById(int id) throws SQLException {
        String sql = "SELECT * FROM journal_entries WHERE entry_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToJournalEntry(rs));
            } else {
                return Optional.empty();
            }
        } finally {
            DatabaseUtil.closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<JournalEntry> findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM journal_entries WHERE user_id = ? ORDER BY entry_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            
            List<JournalEntry> entries = new ArrayList<>();
            while (rs.next()) {
                entries.add(mapResultSetToJournalEntry(rs));
            }
            
            return entries;
        } finally {
            DatabaseUtil.closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<JournalEntry> findByUserIdAndDateRange(int userId, LocalDate startDate, LocalDate endDate) 
            throws SQLException {
        String sql = "SELECT * FROM journal_entries WHERE user_id = ? AND entry_date BETWEEN ? AND ? "
                   + "ORDER BY entry_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(startDate));
            pstmt.setDate(3, Date.valueOf(endDate));
            rs = pstmt.executeQuery();
            
            List<JournalEntry> entries = new ArrayList<>();
            while (rs.next()) {
                entries.add(mapResultSetToJournalEntry(rs));
            }
            
            return entries;
        } finally {
            DatabaseUtil.closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<JournalEntry> findByUserIdAndKeyword(int userId, String keyword) throws SQLException {
        String sql = "SELECT * FROM journal_entries WHERE user_id = ? AND content LIKE ? "
                   + "ORDER BY entry_date DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + keyword + "%");
            rs = pstmt.executeQuery();
            
            List<JournalEntry> entries = new ArrayList<>();
            while (rs.next()) {
                entries.add(mapResultSetToJournalEntry(rs));
            }
            
            return entries;
        } finally {
            DatabaseUtil.closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public boolean update(JournalEntry entry) throws SQLException {
        String sql = "UPDATE journal_entries SET content = ?, mood = ?, updated_at = ? WHERE entry_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, entry.getContent());
            pstmt.setString(2, entry.getMood());
            
            LocalDateTime updatedAt = entry.getUpdatedAt() != null ? entry.getUpdatedAt() : LocalDateTime.now();
            pstmt.setTimestamp(3, Timestamp.valueOf(updatedAt));
            pstmt.setInt(4, entry.getEntryId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeResources(pstmt, conn);
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM journal_entries WHERE entry_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } finally {
            DatabaseUtil.closeResources(pstmt, conn);
        }
    }
    
    private JournalEntry mapResultSetToJournalEntry(ResultSet resultSet) throws SQLException {
        JournalEntry entry = new JournalEntry();
        entry.setEntryId(resultSet.getInt("entry_id"));
        entry.setUserId(resultSet.getInt("user_id"));
        entry.setContent(resultSet.getString("content"));
        entry.setMood(resultSet.getString("mood"));

        // Robustly parse entry_date (DATE in schema, stores LocalDate)
        try {
            Timestamp entryDateTs = resultSet.getTimestamp("entry_date");
            if (entryDateTs != null) {
                entry.setEntryDate(entryDateTs.toLocalDateTime().toLocalDate());
            } else {
                // Fallback if getTimestamp returns null but a value might exist as long
                long entryDateMillis = resultSet.getLong("entry_date");
                if (!resultSet.wasNull()) { // Check if getLong actually found a value
                    entry.setEntryDate(Instant.ofEpochMilli(entryDateMillis).atZone(ZoneId.systemDefault()).toLocalDate());
                }
            }
        } catch (SQLException e) {
            // This catch block might be hit if the column contains a string that's not a valid timestamp format
            // but could be a string representation of a long.
            System.err.println("SQL Error parsing entry_date for entry_id: " + entry.getEntryId() + ". Error: " + e.getMessage() + ". Attempting fallback to long.");
            try {
                long entryDateMillis = resultSet.getLong("entry_date");
                if (!resultSet.wasNull()) {
                    entry.setEntryDate(Instant.ofEpochMilli(entryDateMillis).atZone(ZoneId.systemDefault()).toLocalDate());
                } else {
                     // If it's a string that's not a number either, try parsing directly if a known format.
                     String entryDateStr = resultSet.getString("entry_date");
                     if(entryDateStr != null) {
                        try {
                            // Assuming format YYYY-MM-DD if it was a string from direct getDate on a DATE type
                            entry.setEntryDate(java.time.LocalDate.parse(entryDateStr));
                        } catch (java.time.format.DateTimeParseException dtpe) {
                            System.err.println("Failed to parse entry_date string '" + entryDateStr + "' for entry_id: " + entry.getEntryId());
                        }
                     }
                }
            } catch (SQLException e2) {
                System.err.println("Failed to parse entry_date as long for entry_id: " + entry.getEntryId() + ". Error: " + e2.getMessage());
            }
        }

        // Robustly parse created_at (DATETIME in schema, stores LocalDateTime)
        try {
            Timestamp createdAtTs = resultSet.getTimestamp("created_at");
            if (createdAtTs != null) {
                entry.setCreatedAt(createdAtTs.toLocalDateTime());
            } else {
                long createdAtMillis = resultSet.getLong("created_at");
                if (!resultSet.wasNull()) {
                    entry.setCreatedAt(Instant.ofEpochMilli(createdAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                } else {
                    // Handle cases where created_at might be a string not parseable by getTimestamp but is by getLong
                    // This path is less likely for created_at if it has a DEFAULT CURRENT_TIMESTAMP string value
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error parsing created_at for entry_id: " + entry.getEntryId() + ". Error: " + e.getMessage() + ". Attempting fallback to long.");
            try {
                long createdAtMillis = resultSet.getLong("created_at");
                 if (!resultSet.wasNull()) {
                    entry.setCreatedAt(Instant.ofEpochMilli(createdAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                } else {
                    // If it's a string like "YYYY-MM-DD HH:MM:SS"
                    String createdAtStr = resultSet.getString("created_at");
                    if (createdAtStr != null) {
                        try {
                            // Standard ISO-like format, common for CURRENT_TIMESTAMP
                             entry.setCreatedAt(java.time.LocalDateTime.parse(createdAtStr.replace(" ", "T")));
                        } catch (java.time.format.DateTimeParseException dtpe) {
                             System.err.println("Failed to parse created_at string '" + createdAtStr + "' for entry_id: " + entry.getEntryId());
                        }
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Failed to parse created_at as long for entry_id: " + entry.getEntryId() + ". Error: " + e2.getMessage());
            }
        }
        if (entry.getCreatedAt() == null) {
             System.err.println("Warning: created_at is null for entry_id: " + entry.getEntryId() + " after parsing attempts.");
        }


        // Robustly parse updated_at (DATETIME in schema, stores LocalDateTime)
        try {
            Timestamp updatedAtTs = resultSet.getTimestamp("updated_at");
            if (updatedAtTs != null) {
                entry.setUpdatedAt(updatedAtTs.toLocalDateTime());
            } else {
                long updatedAtMillis = resultSet.getLong("updated_at");
                if (!resultSet.wasNull()) {
                    entry.setUpdatedAt(Instant.ofEpochMilli(updatedAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Error parsing updated_at for entry_id: " + entry.getEntryId() + ". Error: " + e.getMessage() + ". Attempting fallback to long.");
            try {
                long updatedAtMillis = resultSet.getLong("updated_at");
                if (!resultSet.wasNull()) {
                    entry.setUpdatedAt(Instant.ofEpochMilli(updatedAtMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                }
                 // No string fallback for updated_at here, assuming it's either a good timestamp or epoch millis
            } catch (SQLException e2) {
                System.err.println("Failed to parse updated_at as long for entry_id: " + entry.getEntryId() + ". Error: " + e2.getMessage());
            }
        }
        
        return entry;
    }
} 