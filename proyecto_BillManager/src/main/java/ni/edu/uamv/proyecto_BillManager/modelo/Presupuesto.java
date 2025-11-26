package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Month;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

@Entity
@Getter @Setter
@Tab(
        properties="periodo, anio, montoLimite"
)
@View(members="periodo, anio; montoLimite, notas")
public class Presupuesto {

    @Id @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @Required
    private Month periodo;

    @Column(length = 4)
    @Required
    @DefaultValueCalculator(CurrentYearCalculator.class)
    private int anio;

    @Column(name = "monto_limite")
    @Stereotype("MONEY")
    @Required(message = "Define cuánto quieres gastar máximo")
    private BigDecimal montoLimite;

    @Column(length = 100)
    @Stereotype("MEMO")
    private String notas;
}