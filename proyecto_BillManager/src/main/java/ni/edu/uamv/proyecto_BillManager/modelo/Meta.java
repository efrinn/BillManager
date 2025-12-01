package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection; // Importante
import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
// Actualizamos la vista para incluir la lista de transacciones (ahorros)
@View(members=
        "Datos { foto; nombre; montoObjetivo; montoAcumulado; progreso } " +
                "Plazos { fechaLimite; estado } " +
                "Aportes { transacciones }"
)
public class Meta {

    public enum Estado { PROGRESANDO, CUMPLIDA, VENCIDA }

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] foto;

    @Required(message = "El nombre de la meta debe tener valor")
    @Column(name = "nombre_meta", length=60, nullable=false)
    private String nombre;

    @Required
    @Column(name = "montoObjetivo_meta", nullable=false)
    @Stereotype("MONEY")
    @Min(value = 0)
    private BigDecimal montoObjetivo;

    // RELACIÓN: Transacciones que aportan a esta meta
    @OneToMany(mappedBy="meta")
    @ListProperties("fechaTransaccion, nombre, monto")
    private Collection<Transaccion> transacciones;

    // CÁLCULO: Eliminamos el campo persistente y lo hacemos calculado
    @Stereotype("MONEY")
    @Depends("transacciones.monto")
    public BigDecimal getMontoAcumulado() {
        BigDecimal total = BigDecimal.ZERO;
        if (transacciones != null) {
            for (Transaccion t : transacciones) {
                if (t.getMonto() != null) {
                    total = total.add(t.getMonto());
                }
            }
        }
        return total;
    }

    // BARRA DE PROGRESO (Estilo estándar azul)
    @Stereotype("PROGRESS_BAR")
    @Depends("montoObjetivo, transacciones.monto") // Depende ahora de las transacciones
    public BigDecimal getProgreso() {
        BigDecimal acumulado = getMontoAcumulado();

        if (montoObjetivo == null || montoObjetivo.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal progreso = acumulado.divide(montoObjetivo, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        return progreso.min(new BigDecimal("100"));
    }

    @Required
    @Column(name = "fechaLimite_meta", nullable = false)
    @FutureOrPresent
    private LocalDate fechaLimite;

    @Required
    @Enumerated(EnumType.STRING)
    private Estado estado;
}