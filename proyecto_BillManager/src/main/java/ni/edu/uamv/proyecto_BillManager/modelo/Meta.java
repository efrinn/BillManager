package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentUserCalculator;
import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;

@Entity
@Getter @Setter
@Tab(
        filter=FiltroUsuario.class,
        baseCondition="${usuario} = ?",
        properties="nombre, montoObjetivo, montoAcumulado, progreso, fechaLimite, estado",
        defaultOrder="fechaLimite asc"
)
@View(members=
        "Datos { foto; nombre; montoObjetivo; montoAcumulado; progreso } " +
                "Plazos { fechaLimite; estado }"
)
public class Meta {

    public enum Estado { PROGRESANDO, CUMPLIDA, VENCIDA }

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    // --- SEGURIDAD ---
    @Column(length=50)
    @Hidden
    @DefaultValueCalculator(CurrentUserCalculator.class)
    private String usuario;
    // -----------------

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] foto;

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Required
    @Column(nullable=false)
    @Stereotype("MONEY")
    private BigDecimal montoObjetivo;

    @Required
    @Column(nullable=false)
    @Stereotype("MONEY")
    private BigDecimal montoAcumulado = BigDecimal.ZERO;

    @Stereotype("PROGRESS_BAR")
    @Depends("montoAcumulado, montoObjetivo")
    public BigDecimal getProgreso() {
        if (montoObjetivo == null || montoObjetivo.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal progreso = montoAcumulado.divide(montoObjetivo, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));

        // Evitamos que la barra se salga de 100% visualmente
        return progreso.min(new BigDecimal("100"));
    }

    @Required
    private LocalDate fechaLimite;

    @Required
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @PrePersist @PreUpdate
    private void actualizarEstado() {
        if (usuario == null) usuario = org.openxava.util.Users.getCurrent();

        // Lógica automática de estado (opcional)
        if (montoAcumulado.compareTo(montoObjetivo) >= 0) {
            this.estado = Estado.CUMPLIDA;
        } else if (LocalDate.now().isAfter(fechaLimite)) {
            this.estado = Estado.VENCIDA;
        } else {
            this.estado = Estado.PROGRESANDO;
        }
    }
}