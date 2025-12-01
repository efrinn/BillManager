package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
@Tab(
        properties="nombre, montoObjetivo, montoAcumulado, progreso, fechaLimite, estado",
        defaultOrder="fechaLimite asc"
)
@View(members=
        "Datos { foto; nombre; montoObjetivo; montoAcumulado; progreso } " +
                "Plazos { fechaLimite; estado } " +
                "Ahorros { transacciones }" // Mostramos el detalle de los ingresos
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

    @Required(message = "La meta tiene que tener un monto objetivo")
    @Column(name = "montoObjetivo_meta", nullable=false)
    @Stereotype("MONEY")
    @Min(value = 0, message = "No se puede poner un monto negativo")
    private BigDecimal montoObjetivo;

    // --- NUEVO: Lista de transacciones (ahorros) ---
    @OneToMany(mappedBy="meta")
    @ListProperties("fechaTransaccion, nombre, monto")
    @ReadOnly
    private Collection<Transaccion> transacciones;

    // --- MODIFICADO: Ya no es @Column, es calculado ---
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

    // --- BARRA AZUL ESTÁNDAR (Se llena con los ahorros) ---
    @Stereotype("PROGRESS_BAR")
    @Depends("montoObjetivo, transacciones.monto") // Depende de los ahorros
    public BigDecimal getProgreso() {
        BigDecimal acumulado = getMontoAcumulado();

        if (montoObjetivo == null || montoObjetivo.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal progreso = acumulado.divide(montoObjetivo, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // Limitamos a 100% visualmente
        return progreso.min(new BigDecimal("100"));
    }

    @Required(message = "Es obligatorio que la meta tenga una fecha límite")
    @Column(name = "fechaLimite_meta", nullable = false)
    @FutureOrPresent
    private LocalDate fechaLimite;

    @Required(message = "La meta debe tener un estado en específico")
    @Enumerated(EnumType.STRING)
    private Estado estado;
}