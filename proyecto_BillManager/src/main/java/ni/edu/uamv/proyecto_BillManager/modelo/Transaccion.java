package ni.edu.uamv.proyecto_BillManager.modelo;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*; // Importante para @AssertTrue
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
        "Principal { fechaTransaccion; nombre; monto; presupuesto; meta } " +
                "Detalles { descripcion; comprobante }"
)
public class Transaccion {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    // CAMBIO 1: nullable = true para permitir que esté vacío si es una transacción de Meta
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = true)
    private Presupuesto presupuesto;

    // CAMBIO 2: nullable = true para permitir que esté vacío si es una transacción de Presupuesto
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_meta", nullable = true)
    private Meta meta;

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

    @Column(name = "fecha_transaccion")
    @Required
    // 1. Asigna la fecha del sistema automáticamente al abrir el formulario
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    // 2. Bloquea el campo para que el usuario no pueda cambiarlo
    @ReadOnly
    private LocalDate fechaTransaccion;

    @Column(name = "comprobante_transaccion", length=32)
    @Stereotype("FILE")
    private String comprobante;

    // CAMBIO 3: Validación de exclusividad (XOR)
    // Esto asegura que el usuario elija uno, pero no los dos al mismo tiempo.
    @AssertTrue(message = "La transacción debe pertenecer a un Presupuesto O a una Meta, pero no a ambos a la vez.")
    private boolean isDestinoUnico() {
        // Retorna true solo si: (Tengo Presupuesto Y NO Meta) O (NO tengo Presupuesto Y tengo Meta)
        return (presupuesto != null && meta == null) ||
                (presupuesto == null && meta != null);
    }
}