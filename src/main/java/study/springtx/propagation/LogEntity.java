package study.springtx.propagation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LogEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String message;

    public LogEntity() {}

    public LogEntity(String message) {
        this.message = message;
    }
}
