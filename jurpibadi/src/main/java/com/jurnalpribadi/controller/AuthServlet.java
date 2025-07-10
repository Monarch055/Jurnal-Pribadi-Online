package com.jurnalpribadi.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import com.jurnalpribadi.dao.UserDAO;
import com.jurnalpribadi.dao.UserDAOImpl;
import com.jurnalpribadi.model.User;
import com.jurnalpribadi.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "AuthServlet", urlPatterns = {"/register", "/login", "/logout"})
public class AuthServlet extends HttpServlet {
    private UserDAO userDAO;
    
    @Override
    public void init() {
        userDAO = new UserDAOImpl();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/register".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        } else if ("/login".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        } else if ("/logout".equals(path)) {
            handleLogout(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/register".equals(path)) {
            handleRegistration(request, response);
        } else if ("/login".equals(path)) {
            handleLogin(request, response);
        }
    }
    
    private void handleRegistration(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Validation
        boolean hasError = false;
        
        if (username == null || username.trim().isEmpty()) {
            request.setAttribute("usernameError", "Username is required");
            hasError = true;
        }
        
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            request.setAttribute("emailError", "Valid email is required");
            hasError = true;
        }
        
        if (password == null || password.trim().length() < 6) {
            request.setAttribute("passwordError", "Password must be at least 6 characters long");
            hasError = true;
        }
        
        if (!password.equals(confirmPassword)) {
            request.setAttribute("confirmPasswordError", "Passwords do not match");
            hasError = true;
        }
        
        // Check if username or email already exists
        try {
            Optional<User> existingUserByUsername = userDAO.findByUsername(username);
            if (existingUserByUsername.isPresent()) {
                request.setAttribute("usernameError", "Username already exists");
                hasError = true;
            }
            
            Optional<User> existingUserByEmail = userDAO.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                request.setAttribute("emailError", "Email already exists");
                hasError = true;
            }
        } catch (SQLException e) {
            log("Error checking existing user", e);
            request.setAttribute("errorMessage", "Database error occurred");
            hasError = true;
        }
        
        if (hasError) {
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPasswordHash(SecurityUtil.hashPassword(password));
            newUser.setCreatedAt(LocalDateTime.now());
            
            int userId = userDAO.create(newUser);
            
            HttpSession session = request.getSession();
            newUser.setUserId(userId);
            session.setAttribute("user", newUser);
            
            response.sendRedirect(request.getContextPath() + "/journal");
        } catch (SQLException e) {
            log("Error registering user", e);
            request.setAttribute("errorMessage", "Registration failed due to a database error");
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }
    
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validation
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Username and password are required");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            return;
        }
        
        try {
            Optional<User> userOptional = userDAO.findByUsername(username);
            
            if (!userOptional.isPresent() || 
                !SecurityUtil.checkPassword(password, userOptional.get().getPasswordHash())) {
                request.setAttribute("errorMessage", "Invalid username or password");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
                return;
            }
            
            User user = userOptional.get();
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            
            response.sendRedirect(request.getContextPath() + "/journal");
        } catch (SQLException e) {
            log("Error logging in", e);
            request.setAttribute("errorMessage", "Login failed due to a database error");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
    
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }
} 