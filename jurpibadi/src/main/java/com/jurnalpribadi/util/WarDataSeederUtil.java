package com.jurnalpribadi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.servlet.ServletContext;

public class WarDataSeederUtil {

    private static final String SEED_DB_NAME_IN_WAR = "/jurnal_pribadi.db";
    private static final String RUNTIME_DB_NAME = "jurnal_pribadi.db"; // Should match DatabaseUtil.DB_NAME

    public static void seedDatabaseFromWar(ServletContext context) {
        File targetDbFile = new File(RUNTIME_DB_NAME);

        if (targetDbFile.exists()) {
            System.out.println("Runtime database file already exists at: " + targetDbFile.getAbsolutePath() + ". No seeding required.");
            return;
        }

        System.out.println("Runtime database file not found at: " + targetDbFile.getAbsolutePath() + ". Attempting to seed from WAR.");

        try (InputStream sourceDbStream = context.getResourceAsStream(SEED_DB_NAME_IN_WAR)) {
            
            if (sourceDbStream == null) {
                System.err.println("Seed database '" + SEED_DB_NAME_IN_WAR + "' not found in WAR. Cannot seed runtime database.");
                System.err.println("Please ensure '" + RUNTIME_DB_NAME + "' is placed in 'src/main/webapp/' to be included in the WAR root.");
                return;
            }

            File parentDir = targetDbFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    System.out.println("Created parent directory for runtime database: " + parentDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create parent directory for runtime database: " + parentDir.getAbsolutePath() + ". Seeding might fail if current directory is not writable.");
                }
            }
            
            System.out.println("Found seed database '" + SEED_DB_NAME_IN_WAR + "' in WAR. Copying to: " + targetDbFile.getAbsolutePath());

            try (OutputStream targetDbStream = new FileOutputStream(targetDbFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = sourceDbStream.read(buffer)) != -1) {
                    targetDbStream.write(buffer, 0, bytesRead);
                }
                System.out.println("Database successfully seeded from WAR to: " + targetDbFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("Error writing seeded database to: " + targetDbFile.getAbsolutePath());
                e.printStackTrace();
                // Clean up partially written file if copy failed
                if (targetDbFile.exists()) {
                    if (targetDbFile.delete()) {
                        System.out.println("Cleaned up partially written target DB file.");
                    } else {
                        System.err.println("Failed to clean up partially written target DB file.");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error accessing seed database resource stream from WAR: " + SEED_DB_NAME_IN_WAR);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during database seeding preparation.");
            e.printStackTrace();
        }
    }
} 