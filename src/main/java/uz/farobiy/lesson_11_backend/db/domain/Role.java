package uz.farobiy.lesson_11_backend.db.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;


import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String name;
//    @OneToMany(fetch = FetchType.LAZY)
//    private List<User> users;
    @Override
    public String getAuthority() {
        return this.name;
    }
}
