package uz.farobiy.lesson_11_backend.db.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity(name = "files")
public class File implements Serializable {

    @Id
    private String pkey;

    private String name;

    private Long size;

    private String pathUrl;
}
