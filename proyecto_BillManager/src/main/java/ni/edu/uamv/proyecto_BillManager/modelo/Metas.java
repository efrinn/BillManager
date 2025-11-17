package ni.edu.uamv.proyecto_BillManager.modelo;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import java.util.*;
import lombok.*;
import javax.persistence.Table;
@Entity @Getter @Setter
@Table(name = "metas")

public class Metas {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id_meta", length = 36)
    private String idMeta;

    @Column(name = "descripcion", length = 255, nullable = false)
    private String descripcion;

    @Column(name = "fecha_objetivo", nullable = false)
    @Temporal(TemporalType.DATE)

    private Date fechaObjetivo;

    @Column(name = "monto_objetivo", nullable = false)
    private Double montoObjetivo;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    @Required(message = "La meta debe estar asociada a un usuario")
    private Usuario usuario;


    @ManyToOne
    @JoinColumn(name = "id_organizacion", nullable = false
    )@Required (message = "La meta debe estar asociada a una organizacion")
    private Organizacion organizacion;
}
