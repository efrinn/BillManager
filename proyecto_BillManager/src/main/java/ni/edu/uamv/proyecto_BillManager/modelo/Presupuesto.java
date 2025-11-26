package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Month;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

@Entity
@Getter @Setter
@View(members="nombre, periodo, anio; montoLimite, notas")
public class Presupuesto {

    @Id @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(name = "nombre_presupuesto", length = 80, nullable = false)
    @Required(message = "El presupuesto debe tener nombre para entender su contexto")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_presupuesto", length = 15)
    @Required(message = "El presupuesto debe tener un periodo")
    private Month periodo;

    @Column(name = "year_presupuesto", length = 4)
    @Required(message = "El presupuesto debe contener el año en que se obtuvó")
    @DefaultValueCalculator(CurrentYearCalculator.class)
    private int anio;

    @Column(name = "monto_limite", nullable = false)
    @Stereotype("MONEY")
    @Required(message = "Define cuánto quieres gastar máximo")
    @Min(value = 0, message = "No se puede poner un monto negativo")
    private BigDecimal montoLimite;

    @Column(name = "notas_presupuesto", length = 100)
    @Stereotype("MEMO")
    private String notas;
}