package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import javax.persistence.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;

@Entity
@Getter @Setter
@Tab(
        filter=FiltroUsuario.class,
        baseCondition="${usuario} = ?",
        properties="nombre, tipo, totalIngresos, totalGastos, balance",
        defaultOrder="nombre asc"
)
@View(members=
        "Datos del Negocio { logo; nombre; tipo } " +
                "Tablero Financiero { totalIngresos; totalGastos; balance; margenGanancia; semaforo } " +
                "Analisis { gastos }"
)
public class Negocio {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    // Auditoría automática para seguridad
    @Column(length=50)
    @Hidden
    @DefaultValueCalculator(org.openxava.calculators.CurrentUserCalculator.class)
    private String usuario;

    @Column(length=32)
    @Stereotype("PHOTO") // Para logos la foto está bien
    private byte[] logo;

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Column(length=40)
    @Required
    private String tipo;

    // OPTIMIZACIÓN (RF03, RF08): Cálculos directos en base de datos
    // Nota: Las subconsultas asumen que las tablas se llaman 'Transaccion' y 'Categoria'
    // y que las columnas de FK son negocio_id y categoria_id. Ajusta si tu BD es diferente.

    @Formula("(select coalesce(sum(t.monto),0) from Transaccion t " +
            "where t.negocio_id = oid " +
            "and t.categoria_id in (select c.oid from Categoria c where c.tipo = 'INGRESO'))")
    @Stereotype("MONEY")
    private BigDecimal totalIngresos;

    @Formula("(select coalesce(sum(t.monto),0) from Transaccion t " +
            "where t.negocio_id = oid " +
            "and t.categoria_id in (select c.oid from Categoria c where c.tipo = 'GASTO'))")
    @Stereotype("MONEY")
    private BigDecimal totalGastos;

    // El balance también se puede calcular con fórmula para poder ORDENAR por él en la lista
    @Formula("(select ( " +
            "(select coalesce(sum(t.monto),0) from Transaccion t where t.negocio_id = oid and t.categoria_id in (select c.oid from Categoria c where c.tipo = 'INGRESO')) " +
            "- " +
            "(select coalesce(sum(t.monto),0) from Transaccion t where t.negocio_id = oid and t.categoria_id in (select c.oid from Categoria c where c.tipo = 'GASTO')) " +
            ") from INFORMATION_SCHEMA.SYSTEM_USERS limit 1)") // Truco para HSQLDB/Postgres simple, o usar propiedad calculada Java abajo
    @Stereotype("MONEY")
    private BigDecimal balanceCalculadoDB;

    // Versión Java (se muestra en detalle, pero no sirve para ordenar lista eficientemente)
    @Stereotype("MONEY")
    public BigDecimal getBalance() {
        return (totalIngresos == null ? BigDecimal.ZERO : totalIngresos)
                .subtract(totalGastos == null ? BigDecimal.ZERO : totalGastos);
    }

    @Stereotype("MONEY")
    @Depends("totalIngresos, totalGastos")
    public BigDecimal getMargenGanancia() {
        if (totalIngresos == null || totalIngresos.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return getBalance()
                .divide(totalIngresos, 2, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Stereotype("LABEL")
    @Depends("totalIngresos, totalGastos")
    public String getSemaforo() {
        BigDecimal bal = getBalance();
        if (bal.compareTo(BigDecimal.ZERO) > 0)
            return "<span style='color:green;font-weight:bold;'>? SANO</span>";
        if (bal.compareTo(BigDecimal.ZERO) == 0)
            return "<span style='color:orange;font-weight:bold;'>? EQUILIBRADO</span>";
        return "<span style='color:red;font-weight:bold;'>? DÉFICIT</span>";
    }

    // Colecciones para ver detalle (Lazy loading por defecto está bien)
    @OneToMany(mappedBy="negocio")
    @CollectionView("Simple")
    @ListProperties("categoria.nombre, monto, fechaTransaccion")
    private java.util.Collection<Transaccion> gastos;
}