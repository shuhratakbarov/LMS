package uz.farobiy.lesson_11_backend.component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.farobiy.lesson_11_backend.db.domain.*;
import uz.farobiy.lesson_11_backend.db.repository.RoleRepository;
import uz.farobiy.lesson_11_backend.db.repository.admin.UserRepository;

import java.sql.Date;
import java.util.UUID;


@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        Role roleAdmin = new Role(1l, "ROLE_ADMIN");
        Role roleModerator = new Role(2l, "ROLE_TEACHER");
        Role roleUser = new Role(3l, "ROLE_STUDENT");
        try {
            roleRepository.save(roleAdmin);
            roleRepository.save(roleModerator);
            roleRepository.save(roleUser);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        User admin = new User();
        admin.setId(UUID.fromString("330833eb-58df-4935-a49c-84c29ad996c3"));
        admin.setFirstName("Nozimjon");
        admin.setLastName("Qoraboyev");
        admin.setEmail("qoraboyev@gmail.com");
        admin.setPhone("900196171");
        admin.setAddress("Andijon viloyati");
        admin.setBirthDate(Date.valueOf("1998-03-25"));
        admin.setPassword(passwordEncoder.encode("12345"));
        admin.setUsername("nozimjon");
        admin.setRole(roleRepository.findByName("ROLE_ADMIN").get());

        User teacher = new User();
        teacher.setId(UUID.fromString("5f1b295f-3bd1-45cf-adb7-19fea72b5c79"));
        teacher.setFirstName("Shuhrat");
        teacher.setLastName("Akbarov");
        teacher.setEmail("akbarov@gmail.com");
        teacher.setPhone("914686946");
        teacher.setAddress("Qashqadaryo viloyati");
        teacher.setBirthDate(Date.valueOf("2004-11-24"));
        teacher.setPassword(passwordEncoder.encode("12345"));
        teacher.setUsername("teacher");
        teacher.setRole(roleRepository.findByName("ROLE_TEACHER").get());

        User student = new User();
        student.setId(UUID.fromString("e9e5e8bf-beac-465a-aa6a-9d17802d5941"));
        student.setFirstName("Zafar");
        student.setLastName("Ziyatov");
        student.setEmail("ziyatov@gmail.com");
        student.setPhone("911234567");
        student.setAddress("Samarqand viloyati");
        student.setBirthDate(Date.valueOf("2003-11-24"));
        student.setPassword(passwordEncoder.encode("12345"));
        student.setUsername("student");
        student.setRole(roleRepository.findByName("ROLE_STUDENT").get());
        try {
//            userRepository.save(admin);
//            userRepository.save(teacher);
//            userRepository.save(student);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
