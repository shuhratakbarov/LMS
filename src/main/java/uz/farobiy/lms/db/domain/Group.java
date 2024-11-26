package uz.farobiy.lms.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Date;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;
    @Column(unique = true)
    private String name;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    private User teacher;
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_student",
            inverseJoinColumns = @JoinColumn(name = "student_id"),
            joinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private List<User> students;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Date createAt;
    @UpdateTimestamp
    private Date updateAt;
}
