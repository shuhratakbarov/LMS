package uz.farobiy.lms.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.farobiy.lms.db.domain.*;
import uz.farobiy.lms.db.repository.RoleRepository;
import uz.farobiy.lms.db.repository.admin.UserRepository;

import java.sql.Date;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            Role roleAdmin = new Role(1L, "ROLE_ADMIN");
            Role roleModerator = new Role(2L, "ROLE_TEACHER");
            Role roleUser = new Role(3L, "ROLE_STUDENT");
            try {
//                roleRepository.save(roleAdmin);
//                roleRepository.save(roleModerator);
//                roleRepository.save(roleUser);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            User admin = User.builder()
                    .id(UUID.fromString("330833eb-58df-4935-a49c-84c29ad996c3"))
                    .firstName("Nozimjon")
                    .lastName("Qoraboyev")
                    .email("qoraboyev@gmail.com")
                    .phone("900196171")
                    .address("Andijon viloyati")
                    .birthDate(Date.valueOf("1998-03-25"))
                    .password(passwordEncoder.encode("12345"))
                    .username("nozimjon")
                    .role(roleRepository.findByName("ROLE_ADMIN").get())
                    .build();
            User teacher = User.builder()
                    .id(UUID.fromString("5f1b295f-3bd1-45cf-adb7-19fea72b5c79"))
                    .firstName("Shuhrat")
                    .lastName("Akbarov")
                    .email("akbarov@gmail.com")
                    .phone("914686946")
                    .address("Qashqadaryo viloyati")
                    .birthDate(Date.valueOf("2004-11-24"))
                    .password(passwordEncoder.encode("12345"))
                    .username("teacher")
                    .role(roleRepository.findByName("ROLE_TEACHER").get())
                    .build();
            User student = User.builder()
                    .id(UUID.fromString("e9e5e8bf-beac-465a-aa6a-9d17802d5941"))
                    .firstName("Zafar")
                    .lastName("Ziyatov")
                    .email("ziyatov@gmail.com")
                    .phone("911234567")
                    .address("Samarqand viloyati")
                    .birthDate(Date.valueOf("2003-11-24"))
                    .password(passwordEncoder.encode("12345"))
                    .username("student")
                    .role(roleRepository.findByName("ROLE_STUDENT").get())
                    .build();
            try {
//                userRepository.save(admin);
//                userRepository.save(teacher);
//                userRepository.save(student);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
