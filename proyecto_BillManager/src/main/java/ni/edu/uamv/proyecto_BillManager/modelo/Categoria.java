package ni.edu.uamv.proyecto_BillManager.modelo;
import javax.persistence.*;
import org.openxava.annotations.*;
import lombok.*;

@Entity @Getter @Setter
public class Categoria {

    public enum Tipo { ACTIVO, PASIVO }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Required @Column(length = 40)
    private String nombre;

    @Required
    @Enumerated(EnumType.STRING)
    private Tipo tipo;
}
