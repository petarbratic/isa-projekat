package rs.ac.ftn.isa.backend.domain.model;
import jakarta.persistence.*;

@Entity
@Table(name = "view_counter")
public class ViewCounter {

    @Id
    private Long id = 1L;   // jedna vrsta brojača (možeš kasnije proširiti na više)

    @Column(nullable = false)
    private Long value = 0L;

    public Long getId() { return id; }
    public Long getValue() { return value; }
    public void setValue(Long value) { this.value = value; }
}