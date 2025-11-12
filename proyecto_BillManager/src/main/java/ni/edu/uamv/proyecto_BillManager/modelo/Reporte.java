package ni.edu.uamv.proyecto_BillManager.modelo;
import javax.persistence.*;
import lombok.*;

@Entity @Getter @Setter
public class Reporte {

    public enum Tipo { PDF, EXCEL, CSV }

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    private Tipo tipo;

    @Column(length = 120)
    private String nombreArchivo;

    private java.time.LocalDateTime fechaGeneracion;
}
