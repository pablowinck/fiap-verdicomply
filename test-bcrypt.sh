#!/bin/bash
# Test BCrypt password hashing using Docker

docker exec verdicomply-api sh -c '
cat > /tmp/BCryptTest.java << '\''EOF'\''
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

        // Test passwords
        String[] passwords = {"admin123", "gestor123", "auditor123"};

        // Hashes from database
        String adminHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cFQQODkTW2E3JwLx/GQYbq.gFkXGy";
        String gestorHash = "$2a$10$N.dpzV/vYVjW8l0eKCdYZuqTdYdx.KN2mQwQPJBCQ1H7qvmBzPfVS";
        String auditorHash = "$2a$10$7V.tXOIIwWTqEQ2QmI9pYesC0dZU4Y7RrICx7vCqD1LKJnZW0w42S";

        System.out.println("Testing BCrypt password verification:");
        System.out.println("==========================================");

        System.out.println("\nAdmin password (admin123):");
        System.out.println("Hash from DB: " + adminHash);
        System.out.println("Matches: " + encoder.matches("admin123", adminHash));

        System.out.println("\nGestor password (gestor123):");
        System.out.println("Hash from DB: " + gestorHash);
        System.out.println("Matches: " + encoder.matches("gestor123", gestorHash));

        System.out.println("\nAuditor password (auditor123):");
        System.out.println("Hash from DB: " + auditorHash);
        System.out.println("Matches: " + encoder.matches("auditor123", auditorHash));

        System.out.println("\nGenerating new valid hashes:");
        System.out.println("==========================================");
        for (String pwd : passwords) {
            String newHash = encoder.encode(pwd);
            System.out.println("Password: " + pwd);
            System.out.println("New hash: " + newHash);
            System.out.println("Verification: " + encoder.matches(pwd, newHash));
            System.out.println();
        }
    }
}
EOF

cd /tmp
javac -cp "/app/lib/*" BCryptTest.java
java -cp "/app/lib/*:." BCryptTest
'
