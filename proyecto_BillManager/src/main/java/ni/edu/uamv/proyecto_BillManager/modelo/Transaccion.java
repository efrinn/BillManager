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
import org.openxava.util.Users; // [IMPORTANTE]

@Entity
@Getter @Setter
@Tab(
        properties="fechaTransaccion, nombre, monto",
        defaultOrder="fechaTransaccion desc",
        baseCondition = "${usuario} = ?" // [FILTRO] Solo muestra transacciones del usuario logueado
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

    // --- SEGURIDAD: PROPIEDAD DE USUARIO ---
    @Column(length = 50)
    @Hidden
    private String usuario;

    @PrePersist
    public void onPrePersist() {
        if (usuario == null) {
            usuario = Users.getCurrent();
        }
    }
    // ---------------------------------------

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = true)
    private Presupuesto presupuesto;

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
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    @ReadOnly
    private LocalDate fechaTransaccion;

    @Column(name = "comprobante_transaccion", length=32)
    @Stereotype("FILE")
    private String comprobante;

    @AssertTrue(message = "La transacción debe pertenecer a un Presupuesto O a una Meta, pero no a ambos a la vez.")
    private boolean isDestinoUnico() {
        return (presupuesto != null && meta == null) ||
                (presupuesto == null && meta != null);
    }
}