package com.jurnalpribadi.listener;

import com.jurnalpribadi.util.WarDataSeederUtil;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Starting Personal Journal application...");
        
        ServletContext context = sce.getServletContext();
        WarDataSeederUtil.seedDatabaseFromWar(context); // Seed database jika perlu

        // Pastikan kelas DatabaseUtil dimuat, yang akan menjalankan inisialisasi statiknya
        // untuk menyiapkan tabel pada database (yang mungkin baru saja di-seed)
        try {
            Class.forName("com.jurnalpribadi.util.DatabaseUtil");
        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: Failed to load DatabaseUtil class in listener. Database might not be initialized correctly.");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Shutting down Personal Journal application...");
    }
} 