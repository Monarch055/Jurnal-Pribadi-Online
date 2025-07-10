package com.jurnalpribadi.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import com.jurnalpribadi.dao.JournalEntryDAO;
import com.jurnalpribadi.dao.JournalEntryDAOImpl;
import com.jurnalpribadi.model.JournalEntry;
import com.jurnalpribadi.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "JournalServlet", urlPatterns = {"/journal", "/journal/*"})
public class JournalServlet extends HttpServlet {
    
    private JournalEntryDAO journalEntryDAO;
    
    @Override
    public void init() {
        journalEntryDAO = new JournalEntryDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || "/".equals(pathInfo)) {
            // List all entries
            try {
                List<JournalEntry> entries = journalEntryDAO.findByUserId(user.getUserId());
                request.setAttribute("entries", entries);
                request.getRequestDispatcher("/WEB-INF/views/journal/list.jsp").forward(request, response);
            } catch (SQLException e) {
                log("Error retrieving journal entries", e);
                request.setAttribute("errorMessage", "Failed to retrieve journal entries");
                request.getRequestDispatcher("/WEB-INF/views/journal/list.jsp").forward(request, response);
            }
        } else if ("/new".equals(pathInfo)) {
            // Show form to create a new entry
            JournalEntry newEntry = new JournalEntry();
            newEntry.setUserId(user.getUserId());
            newEntry.setEntryDate(LocalDate.now());
            request.setAttribute("entry", newEntry);
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            // Show form to edit an existing entry
            try {
                int entryId = Integer.parseInt(pathInfo.substring(6));
                Optional<JournalEntry> entryOptional = journalEntryDAO.findById(entryId);
                
                if (entryOptional.isPresent()) {
                    JournalEntry entry = entryOptional.get();
                    
                    // Verify that the entry belongs to the current user
                    if (entry.getUserId() != user.getUserId()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    
                    request.setAttribute("entry", entry);
                    request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (SQLException e) {
                log("Error retrieving journal entry for editing", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if (pathInfo.startsWith("/view/")) {
            // View a specific entry
            try {
                int entryId = Integer.parseInt(pathInfo.substring(6));
                Optional<JournalEntry> entryOptional = journalEntryDAO.findById(entryId);
                
                if (entryOptional.isPresent()) {
                    JournalEntry entry = entryOptional.get();
                    
                    // Verify that the entry belongs to the current user
                    if (entry.getUserId() != user.getUserId()) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN);
                        return;
                    }
                    
                    request.setAttribute("entry", entry);
                    
                    // Extra logging to help diagnose issues
                    log("Viewing entry ID: " + entry.getEntryId());
                    log("Entry content length: " + (entry.getContent() != null ? entry.getContent().length() : "null"));
                    log("Entry createdAt: " + entry.getCreatedAt());
                    log("Entry updatedAt: " + entry.getUpdatedAt());
                    
                    request.getRequestDispatcher("/WEB-INF/views/journal/view.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                log("Invalid entry ID format", e);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (SQLException e) {
                log("Error retrieving journal entry for viewing: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (Exception e) {
                log("Unexpected error when viewing journal entry: " + e.getMessage(), e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if ("/search".equals(pathInfo)) {
            // Handle search requests
            String keyword = request.getParameter("keyword");
            String startDateStr = request.getParameter("startDate");
            String endDateStr = request.getParameter("endDate");
            
            try {
                List<JournalEntry> entries;
                
                if (keyword != null && !keyword.trim().isEmpty()) {
                    // Search by keyword
                    entries = journalEntryDAO.findByUserIdAndKeyword(user.getUserId(), keyword);
                    request.setAttribute("keyword", keyword);
                } else if (startDateStr != null && !startDateStr.isEmpty() && 
                           endDateStr != null && !endDateStr.isEmpty()) {
                    // Search by date range
                    LocalDate startDate = LocalDate.parse(startDateStr);
                    LocalDate endDate = LocalDate.parse(endDateStr);
                    entries = journalEntryDAO.findByUserIdAndDateRange(user.getUserId(), startDate, endDate);
                    request.setAttribute("startDate", startDateStr);
                    request.setAttribute("endDate", endDateStr);
                } else {
                    // Default to all entries
                    entries = journalEntryDAO.findByUserId(user.getUserId());
                }
                
                request.setAttribute("entries", entries);
                request.getRequestDispatcher("/WEB-INF/views/journal/list.jsp").forward(request, response);
            } catch (SQLException e) {
                log("Error searching journal entries", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (DateTimeParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date format");
            }
        } else if (pathInfo.startsWith("/delete/")) {
            // Handle delete operation if called from GET (redirect to view with error message)
            request.setAttribute("errorMessage", "Please use the Delete button on the view page to delete entries.");
            response.sendRedirect(request.getContextPath() + "/journal");
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        User user = (User) session.getAttribute("user");
        String pathInfo = request.getPathInfo();
        
        if ("/new".equals(pathInfo)) {
            // Create a new entry
            handleCreateEntry(request, response, user);
        } else if (pathInfo != null && pathInfo.startsWith("/edit/")) {
            // Update an existing entry
            handleUpdateEntry(request, response, user, pathInfo);
        } else if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            // Delete an entry
            handleDeleteEntry(request, response, user, pathInfo);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void handleCreateEntry(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        String entryDateStr = request.getParameter("entryDate");
        String content = request.getParameter("content");
        String mood = request.getParameter("mood");
        
        boolean hasError = validateEntry(request, entryDateStr, content);
        
        if (hasError) {
            JournalEntry newEntry = new JournalEntry();
            newEntry.setUserId(user.getUserId());
            try {
                if (entryDateStr != null && !entryDateStr.trim().isEmpty()) {
                    newEntry.setEntryDate(LocalDate.parse(entryDateStr));
                } else {
                    newEntry.setEntryDate(LocalDate.now());
                }
            } catch (DateTimeParseException e) {
                newEntry.setEntryDate(LocalDate.now());
            }
            if (content != null) {
                newEntry.setContent(content);
            }
            if (mood != null) {
                newEntry.setMood(mood);
            }
            request.setAttribute("entry", newEntry);
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
            return;
        }
        
        try {
            JournalEntry entry = new JournalEntry();
            entry.setUserId(user.getUserId());
            entry.setEntryDate(LocalDate.parse(entryDateStr));
            entry.setContent(content);
            entry.setMood(mood);
            
            LocalDateTime now = LocalDateTime.now();
            entry.setCreatedAt(now);
            entry.setUpdatedAt(now);
            
            int entryId = journalEntryDAO.create(entry);
            entry.setEntryId(entryId);  // Set the generated ID to the entry
            
            // Redirect to view the newly created entry instead of the list
            response.sendRedirect(request.getContextPath() + "/journal/view/" + entryId);
        } catch (SQLException e) {
            log("Error creating journal entry: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Failed to create journal entry: " + e.getMessage());
            
            JournalEntry newEntry = new JournalEntry();
            newEntry.setUserId(user.getUserId());
            newEntry.setEntryDate(LocalDate.now());
            if (content != null) {
                newEntry.setContent(content);
            }
            if (mood != null) {
                newEntry.setMood(mood);
            }
            request.setAttribute("entry", newEntry);
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
        } catch (DateTimeParseException e) {
            request.setAttribute("dateError", "Invalid date format");
            
            JournalEntry newEntry = new JournalEntry();
            newEntry.setUserId(user.getUserId());
            newEntry.setEntryDate(LocalDate.now());
            if (content != null) {
                newEntry.setContent(content);
            }
            if (mood != null) {
                newEntry.setMood(mood);
            }
            request.setAttribute("entry", newEntry);
            request.setAttribute("today", LocalDate.now());
            request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
        }
    }
    
    private void handleUpdateEntry(HttpServletRequest request, HttpServletResponse response, User user, String pathInfo) 
            throws ServletException, IOException {
        
        try {
            int entryId = Integer.parseInt(pathInfo.substring(6));
            Optional<JournalEntry> entryOptional = journalEntryDAO.findById(entryId);
            
            if (!entryOptional.isPresent()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            JournalEntry entry = entryOptional.get();
            
            // Verify that the entry belongs to the current user
            if (entry.getUserId() != user.getUserId()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            String content = request.getParameter("content");
            String mood = request.getParameter("mood");
            
            boolean hasError = validateEntry(request, entry.getEntryDate().toString(), content);
            
            if (hasError) {
                request.setAttribute("entry", entry);
                request.getRequestDispatcher("/WEB-INF/views/journal/form.jsp").forward(request, response);
                return;
            }
            
            entry.setContent(content);
            entry.setMood(mood);
            entry.setUpdatedAt(LocalDateTime.now());
            
            journalEntryDAO.update(entry);
            
            response.sendRedirect(request.getContextPath() + "/journal/view/" + entryId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            log("Error updating journal entry", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void handleDeleteEntry(HttpServletRequest request, HttpServletResponse response, User user, String pathInfo) 
            throws ServletException, IOException {
        
        try {
            int entryId = Integer.parseInt(pathInfo.substring(8));
            log("Deleting entry ID: " + entryId);
            
            Optional<JournalEntry> entryOptional = journalEntryDAO.findById(entryId);
            
            if (!entryOptional.isPresent()) {
                log("Entry not found for deletion: " + entryId);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            JournalEntry entry = entryOptional.get();
            
            // Verify that the entry belongs to the current user
            if (entry.getUserId() != user.getUserId()) {
                log("Unauthorized deletion attempt for entry: " + entryId + " by user: " + user.getUserId());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            
            boolean success = journalEntryDAO.delete(entryId);
            
            if (success) {
                log("Entry deleted successfully: " + entryId);
                // Set success message in the session to be displayed after redirect
                HttpSession session = request.getSession();
                session.setAttribute("successMessage", "Journal entry deleted successfully.");
            } else {
                log("Failed to delete entry: " + entryId);
                HttpSession session = request.getSession();
                session.setAttribute("errorMessage", "Failed to delete journal entry.");
            }
            
            response.sendRedirect(request.getContextPath() + "/journal");
        } catch (NumberFormatException e) {
            log("Invalid entry ID format for deletion", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } catch (SQLException e) {
            log("Error deleting journal entry: " + e.getMessage(), e);
            request.setAttribute("errorMessage", "Error deleting journal entry: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/journal/list.jsp").forward(request, response);
        } catch (Exception e) {
            log("Unexpected error when deleting entry: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private boolean validateEntry(HttpServletRequest request, String entryDateStr, String content) {
        boolean hasError = false;
        
        if (entryDateStr == null || entryDateStr.trim().isEmpty()) {
            request.setAttribute("dateError", "Date is required");
            hasError = true;
        } else {
            try {
                LocalDate.parse(entryDateStr);
            } catch (DateTimeParseException e) {
                request.setAttribute("dateError", "Invalid date format");
                hasError = true;
            }
        }
        
        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("contentError", "Content is required");
            hasError = true;
        }
        
        if (hasError) {
            request.setAttribute("entryDate", entryDateStr);
            request.setAttribute("content", content);
            request.setAttribute("mood", request.getParameter("mood"));
        }
        
        return hasError;
    }
} 