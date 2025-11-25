package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
@Tab(
        properties="nombre, tipo, usuarioPropietario.nombre, totalIngresos, totalGastos, balance",
        defaultOrder="nombre asc"
)
@View(members=
        "Datos del Negocio { logo; nombre; tipo; usuarioPropietario } " +
                "Tablero Financiero { totalIngresos; totalGastos; balance; margenGanancia; semaforo } " +
                "Analisis de Gastos { gastos } " +
                "Historial Completo { transacciones }"
)
public class Negocio {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] logo;

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Column(length=40)
    private String tipo;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id")
    @ReferenceView("Simple")
    private Persona usuarioPropietario;

    @OneToMany(mappedBy="negocio")
    @CollectionView("Simple")
    private Collection<Transaccion> transacciones;

    @OneToMany(mappedBy="negocio")
    @Condition("${categoria.tipo} = 'GASTO'")
    @CollectionView("Simple")
    @ListProperties("categoria.nombre, monto, fechaTransaccion")
    private Collection<Transaccion> gastos;

    // ---------- CÁLCULOS ----------
    @Stereotype("MONEY")
    public BigDecimal getTotalIngresos() {
        BigDecimal total = BigDecimal.ZERO;
        if (transacciones == null) return total;
        for (Transaccion t : transacciones)
            if (t.getCategoria() != null && t.getCategoria().getTipo() == Categoria.Tipo.INGRESO)
                total = total.add(t.getMonto());
        return total;
    }

    @Stereotype("MONEY")
    public BigDecimal getTotalGastos() {
        BigDecimal total = BigDecimal.ZERO;
        if (transacciones == null) return total;
        for (Transaccion t : transacciones)
            if (t.getCategoria() != null && t.getCategoria().getTipo() == Categoria.Tipo.GASTO)
                total = total.add(t.getMonto());
        return total;
    }

    @Stereotype("MONEY")
    public BigDecimal getBalance() {
        return getTotalIngresos().subtract(getTotalGastos());
    }

    @Stereotype("MONEY")
    @Depends("totalIngresos, totalGastos")
    public BigDecimal getMargenGanancia() {
        if (getTotalIngresos().compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        return getBalance()
                .divide(getTotalIngresos(), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    @Stereotype("LABEL")
    @Depends("balance")
    public String getSemaforo() {
        if (getBalance().compareTo(BigDecimal.ZERO) > 0)
            return "<span style='color:#16a34a;font-weight:bold;'>? Verde</span>";

        if (getBalance().compareTo(BigDecimal.ZERO) == 0)
            return "<span style='color:#f59e0b;font-weight:bold;'>? Amarillo</span>";

        return "<span style='color:#dc2626;font-weight:bold;'>? Rojo</span>";
    }
}
