package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Meta {

    private enum Estado {Progresando, Cumplida, Vencida}

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Persona usuarioResponsable;

    @Column(name = "nombre_cuenta", length = 60, nullable = false)
    @Required(message = "La cuenta tiene que tener un nombre")
    private String nombre;

    @Column(name = "monto_objetivo", nullable = false)
    @Required(message = "Debe especificar un monto para la meta")
    private double montoObjetivo;

    @Column(name = "monto_acumulado", nullable = false)
    @Required
    private double montoAcumulado = 0;

    @Column(name = "fecha_limite", nullable = false)
    @Required(message = "Debe especificar una fecha para la meta")
    private LocalDate fechaLimite;

    @Column(name = "estado", nullable = false)
    private Estado estado;
}
