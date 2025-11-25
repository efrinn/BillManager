package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
@Tab(
        properties="nombre, usuarioResponsable.nombre, montoObjetivo, montoAcumulado, progreso, fechaLimite, estado",
        defaultOrder="fechaLimite asc"
)
@View(members=
        "Datos { foto; usuarioResponsable; nombre; montoObjetivo; montoAcumulado; progreso } " +
                "Plazos { fechaLimite; estado }"
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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    @DescriptionsList(descriptionProperties="nombre")
    private Persona usuarioResponsable;

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
        if (montoObjetivo == null || montoObjetivo.equals(BigDecimal.ZERO)) return BigDecimal.ZERO;
        return montoAcumulado.divide(montoObjetivo, 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Required
    private LocalDate fechaLimite;

    @Required
    @Enumerated(EnumType.STRING)
    private Estado estado;
}
