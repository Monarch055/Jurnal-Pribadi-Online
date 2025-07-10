package com.jurnalpribadi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JournalEntry {
    private int entryId;
    private int userId;
    private LocalDate entryDate;
    private String content;
    private String mood;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public JournalEntry() {
    }
    
    public JournalEntry(int entryId, int userId, LocalDate entryDate, String content, String mood, 
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.entryId = entryId;
        this.userId = userId;
        this.entryDate = entryDate;
        this.content = content;
        this.mood = mood;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 