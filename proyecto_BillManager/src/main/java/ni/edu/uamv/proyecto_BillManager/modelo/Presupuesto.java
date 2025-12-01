package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.Collection; // Importante
import javax.persistence.*;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

@Entity
@Getter @Setter
// Agregamos 'barraProgreso' y 'gastoTotal' a la vista
@View(members="nombre, periodo, anio; montoLimite, gastoTotal; barraProgreso; notas; transacciones")
public class Presupuesto {

    @Id @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(name = "nombre_presupuesto", length = 80, nullable = false)
    @Required(message = "El presupuesto debe tener nombre")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "periodo_presupuesto", length = 15)
    @Required
    private Month periodo;

    @Column(name = "year_presupuesto", length = 4)
    @DefaultValueCalculator(CurrentYearCalculator.class)
    private int anio;

    @Column(name = "monto_limite", nullable = false)
    @Stereotype("MONEY")
    @Min(value = 0)
    private BigDecimal montoLimite;

    @Column(name = "notas_presupuesto", length = 100)
    @Stereotype("MEMO")
    private String notas;

    // RELACIÓN: Para saber qué transacciones pertenecen a este presupuesto
    @OneToMany(mappedBy="presupuesto")
    @ListProperties("fechaTransaccion, nombre, monto") // Mostramos lista simple
    private Collection<Transaccion> transacciones;

    // CÁLCULO: Suma automática de los gastos
    @Stereotype("MONEY")
    @Depends("transacciones.monto") // Recalcular si cambian los montos
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

    // SEMÁFORO: Barra de colores (Verde -> Amarillo -> Rojo)
    @Stereotype("HTML_TEXT") // Usamos HTML para dibujar la barra con colores
    @Depends("montoLimite, transacciones.monto")
    public String getBarraProgreso() {
        if (montoLimite == null || montoLimite.compareTo(BigDecimal.ZERO) == 0) return "";

        BigDecimal total = getGastoTotal();
        // Calcular porcentaje: (Gasto / Limite) * 100
        BigDecimal porcentaje = total.divide(montoLimite, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));

        // Lógica de colores
        String colorClass = "bg-success"; // Verde por defecto
        if (porcentaje.compareTo(new BigDecimal(100)) >= 0) {
            colorClass = "bg-danger"; // Rojo si se pasa o llega al 100%
        } else if (porcentaje.compareTo(new BigDecimal(80)) >= 0) {
            colorClass = "bg-warning"; // Amarillo si pasa del 80%
        }

        // Limitar visualmente al 100% para que no rompa el diseño
        BigDecimal ancho = porcentaje.min(new BigDecimal(100));

        // HTML usando clases de Bootstrap (incluido en OpenXava)
        return "<div class='progress' style='height: 20px; background-color: #e9ecef; border-radius: 5px;'>" +
                "<div class='progress-bar " + colorClass + "' role='progressbar' " +
                "style='width: " + ancho + "%; transition: width 0.6s ease;' " +
                "aria-valuenow='" + ancho + "' aria-valuemin='0' aria-valuemax='100'>" +
                porcentaje + "%</div></div>";
    }
}