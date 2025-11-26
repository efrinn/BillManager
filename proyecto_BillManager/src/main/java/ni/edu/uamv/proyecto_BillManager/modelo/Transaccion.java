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
        "Principal { fechaTransaccion; nombre; monto } " +
                "Detalles { descripcion; comprobante }"
)
public class Transaccion {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Column(length = 250)
    @Stereotype("MEMO")
    private String descripcion;

    @Required
    @Stereotype("MONEY")
    @Min(0)
    private BigDecimal monto;

    @Required
    @DefaultValueCalculator(CurrentDateCalculator.class)
    private LocalDate fechaTransaccion;

    // MEJORA: FILE permite subir PDF, Imágenes, Excel, etc.
    // Guarda un ID (String), no el binario pesado en la tabla principal.
    @Column(length=32)
    @Stereotype("FILE")
    private String comprobante;
}