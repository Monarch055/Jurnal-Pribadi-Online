package com.jurnalpribadi.util;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    // Buat Hash
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }
    
    // Cek Password sebelum apakah sama dengan hash yg dibuat
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
} 