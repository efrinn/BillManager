package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

@Entity
@Getter @Setter
@Tab(
        properties="fechaTransaccion, nombre, monto",
        defaultOrder="fechaTransaccion desc"
)
@View(members=
        "Principal { fechaTransaccion; nombre; monto; presupuesto } " +
                "Detalles { descripcion; comprobante }"
)
public class Transaccion {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = false)
    private Presupuesto presupuesto;

    @Required(message = "La transaccion debe llevar nombre")
    @Column(name = "nombre_transaccion", length=60, nullable=false)
    private String nombre;

    @Column(name = "desc_transaccion", length = 250)
    @Stereotype("MEMO")
    private String descripcion;

    @Column(name = "monto_transaccion", nullable = false)
    @Required(message = "La transaccion debe tener un monto")
    @Stereotype("MONEY")
    @Min(value = 0, message = "EL monto no puede ser negativo")
    private BigDecimal monto;

    @Column(name = "fecha_transaccion", nullable = false)
    @Required(message = "Es obligatorio que la transaccion tenga la fecha...")

    private LocalDate fechaTransaccion = LocalDate.now();

    // MEJORA: FILE permite subir PDF, Imágenes, Excel, etc.
    // Guarda un ID (String), no el binario pesado en la tabla principal.
    @Column(name = "comprobante_transaccion", length=32)
    @Stereotype("FILE")
    private String comprobante;
}