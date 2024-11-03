package uz.farobiy.lms_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import uz.farobiy.lms_clone.config.SecurityConfig;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"uz.farobiy.lms_clone.db.repository"})
public class LMSClone {
    public static void main(String[] args) {
        SpringApplication.run(LMSClone.class, args);
    }

}
