package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.util.Users;

@Entity
@Getter @Setter
@View(members="nombre, periodo, anio; montoLimite, gastoTotal; estadoPresupuesto; notas; transacciones")
// [MODIFICADO] Se eliminó el @Tab con baseCondition para visibilidad total
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
    @Required(message = "El presupuesto debe contener el año en que se obtuvo")
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

    @OneToMany(mappedBy="presupuesto")
    @ListProperties("fechaTransaccion, nombre, monto")
    @ReadOnly
    private Collection<Transaccion> transacciones;

    @Stereotype("MONEY")
    @Depends("transacciones.monto")
    public BigDecimal getGastoTotal() {
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

    @Depends("montoLimite, transacciones.monto")
    @Stereotype("HTML_TEXT")
    public String getEstadoPresupuesto() {
        if (montoLimite == null || montoLimite.compareTo(BigDecimal.ZERO) == 0) return "";

        BigDecimal gasto = getGastoTotal();
        BigDecimal porcentaje = gasto.divide(montoLimite, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        String color = "#28a745";
        if (porcentaje.compareTo(new BigDecimal(100)) >= 0) {
            color = "#dc3545";
        } else if (porcentaje.compareTo(new BigDecimal(75)) >= 0) {
            color = "#ffc107";
        }

        double anchoVisual = Math.min(porcentaje.doubleValue(), 100.0);

        return "<div style='width: 100%; background-color: #e9ecef; border-radius: 4px; margin-top: 5px;'>" +
                "  <div style='width: " + anchoVisual + "%; background-color: " + color + "; height: 20px; border-radius: 4px; text-align: center; color: white; line-height: 20px; font-size: 12px; font-weight: bold; transition: width 0.5s;'>" +
                porcentaje.intValue() + "%" +
                "  </div>" +
                "</div>";
    }
}