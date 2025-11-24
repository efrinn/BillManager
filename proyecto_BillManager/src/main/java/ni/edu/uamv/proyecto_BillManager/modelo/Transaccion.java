package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Transaccion {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Persona usuarioAutor;

    @Column(length = 60, name = "nombre_transaccion", nullable = false)
    @Required(message = "La transacción debe tener un nombre")
    private String nombre;

    @Column(name = "monto_transaccion", nullable = false)
    @Required(message = "Es obligatorio especificar un monto")
    private double monto;

    @Column(name = "fecha_transaccion", nullable = false)
    @Required(message = "Es obligatorio especificar la fecha en que se realizó la transacción")
    private LocalDate fechaTransaccion;

    @Column(length = 100, name = "desc_transaccion", nullable = false)
    private String descripcion;
}

