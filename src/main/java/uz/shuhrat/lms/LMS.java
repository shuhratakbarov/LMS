package uz.shuhrat.lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"uz.shuhrat.lms.db.repository"})
@EnableScheduling
public class LMS {
    public static void main(String[] args) {
        SpringApplication.run(LMS.class, args);
    }
}
