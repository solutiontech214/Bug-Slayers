package com.logger.backend.config;

import com.logger.backend.model.User;
import com.logger.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseInitializer {

    // Initialize Default Admin User and Apply PostgreSQL FTS Schema
    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, 
                                          PasswordEncoder passwordEncoder,
                                          JdbcTemplate jdbcTemplate) {
        return args -> {
            System.out.println("🔥 Initializing Database...");

            // 1. Create Default Global Admin
            String adminEmail = "admin@bugslayers.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole(User.Role.GLOBAL_ADMIN);
                userRepository.save(admin);
                System.out.println("✅ Default Admin Created: admin@bugslayers.com / admin123");
            }

            // 2. Create Default Project Manager
            String managerEmail = "manager@bugslayers.com";
            if (!userRepository.existsByEmail(managerEmail)) {
                User manager = new User();
                manager.setName("Project Manager");
                manager.setEmail(managerEmail);
                manager.setPassword(passwordEncoder.encode("admin123"));
                manager.setRole(User.Role.PROJECT_MANAGER);
                userRepository.save(manager);
                System.out.println("✅ Default Manager Created: manager@bugslayers.com / admin123");
            }

            // 3. Create Default Developer
            String devEmail = "dev@bugslayers.com";
            if (!userRepository.existsByEmail(devEmail)) {
                User dev = new User();
                dev.setName("Dev User");
                dev.setEmail(devEmail);
                dev.setPassword(passwordEncoder.encode("admin123"));
                dev.setRole(User.Role.DEVELOPER);
                userRepository.save(dev);
                System.out.println("✅ Default Developer Created: dev@bugslayers.com / admin123");
            }

            // 2. Setup PostgreSQL Full-Text Search Vector if needed
            try {
                // Check if search_vector column exists
                String checkSql = "SELECT 1 FROM information_schema.columns WHERE table_name='logs' AND column_name='search_vector'";
                var result = jdbcTemplate.queryForList(checkSql);
                
                if (result.isEmpty()) {
                    jdbcTemplate.execute("ALTER TABLE logs ADD COLUMN search_vector tsvector");
                    System.out.println("✅ Added search_vector column to logs table.");
                }

                // Create GIN index
                jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_logs_search_gin ON logs USING GIN(search_vector)");
                
                // Create Trigger for auto-updating search vector
                jdbcTemplate.execute("""
                    CREATE OR REPLACE FUNCTION logs_search_vector_update()
                    RETURNS trigger AS $$
                    BEGIN
                        NEW.search_vector := to_tsvector('english', coalesce(NEW.message, ''));
                        RETURN NEW;
                    END;
                    $$ LANGUAGE plpgsql;
                """);
                
                jdbcTemplate.execute("DROP TRIGGER IF EXISTS logs_search_vector_trigger ON logs");
                jdbcTemplate.execute("""
                    CREATE TRIGGER logs_search_vector_trigger
                        BEFORE INSERT OR UPDATE ON logs
                        FOR EACH ROW EXECUTE FUNCTION logs_search_vector_update();
                """);
                
                System.out.println("✅ PostgreSQL FTS schema initialized.");
            } catch (Exception e) {
                System.err.println("⚠️ Could not initialize FTS Schema. Are you running PostgreSQL? " + e.getMessage());
            }
        };
    }
}
