package scooterrent.config;

import scooterrent.exception.BusinessException;
import scooterrent.entity.Role;
import scooterrent.entity.Scooter;
import scooterrent.entity.User;
import scooterrent.enums.ScooterStatus;
import scooterrent.repository.RoleRepository;
import scooterrent.repository.ScooterRepository;
import scooterrent.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 应用启动时初始化基础数据（角色、用户、滑板车）
 */
@Component
public class SQLiteDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SQLiteDataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ScooterRepository scooterRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SQLiteDataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                                  ScooterRepository scooterRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.scooterRepository = scooterRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            initRoles();
            initUsers();
            initScooters();
            logger.info("Data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during data initialization: ", e);
        }
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setDescription("Administrator role");
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setDescription("User role");
            roleRepository.save(userRole);

            Role discountRole = new Role();
            discountRole.setName("ROLE_DISCOUNT");
            discountRole.setDescription("Student/Elderly Discount role");
            roleRepository.save(discountRole);
        }
    }

    private void initUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new BusinessException(500, "Admin role not found"));
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BusinessException(500, "User role not found"));
            user.setRole(userRole);
            userRepository.save(user);
        }
    }

    private void initScooters() {
        if (scooterRepository.count() > 0) {
            return;
        }

        Scooter s1 = createScooter("Xiaomi Pro 2", "XP2-2025-001", 15.0, 50.0, 100.0, 500.0, 85, "春熙路", 30.6571, 104.0870, "/images/scooter1.jpg");
        Scooter s2 = createScooter("Ninebot Max", "NBM-2025-002", 18.0, 60.0, 120.0, 600.0, 70, "太古里", 30.6574, 104.0885, "/images/scooter2.jpg");
        Scooter s3 = createScooter("Segway ES4", "SES4-2025-003", 20.0, 70.0, 140.0, 700.0, 45, "宽窄巷子", 30.6705, 104.0647, "/images/scooter3.jpg");
        Scooter s4 = createScooter("Xiaomi Essential", "XE-2025-004", 12.0, 40.0, 80.0, 400.0, 92, "天府广场", 30.6571, 104.0657, "/images/scooter4.jpg");
        Scooter s5 = createScooter("Dualtron Eagle", "DE-2025-005", 25.0, 80.0, 160.0, 800.0, 65, "锦里古街", 30.6421, 104.0471, "/images/scooter5.jpg");

        scooterRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5));
        logger.info("Initialized 5 default scooters");
    }

    private Scooter createScooter(String model, String serialNumber, double perHour, double perFourHours,
                                   double perDay, double perWeek, int battery, String location,
                                   double lat, double lng, String imageUrl) {
        Scooter scooter = new Scooter();
        scooter.setModel(model);
        scooter.setSerialNumber(serialNumber);
        scooter.setStatus(ScooterStatus.AVAILABLE);
        scooter.setPricePerHour(perHour);
        scooter.setPricePerFourHours(perFourHours);
        scooter.setPricePerDay(perDay);
        scooter.setPricePerWeek(perWeek);
        scooter.setBatteryLevel(battery);
        scooter.setLocation(location);
        scooter.setLatitude(lat);
        scooter.setLongitude(lng);
        scooter.setImageUrl(imageUrl);
        return scooter;
    }
} 