package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

import org.openxava.annotations.*;

@Entity
@Getter @Setter
@View(members =
        "usuario, nombre, saldoInicial, saldoActual;" + // fila 1
                "metas;" +                                      // fila 2: colección
                "transacciones"                                // fila 3: colección
)
@Tab(properties = "usuario.nombre, nombre, saldoActual")
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    @Required
    @Column(length = 40)
    private String nombre;

    private double saldoInicial;
    private double saldoActual;

    // --- Relación con Meta ---
    @OneToMany(mappedBy = "cuenta")
    @ListProperties("nombre, montoObjetivo, montoAcumulado, estado")
    private Collection<Metas> metas;

    // --- Relación con Transaccion + gráfico ---
    @OneToMany(mappedBy = "cuenta")
    @ListProperties("fecha, descripcion, categoria.nombre, monto")
    private Collection<Transaccion> transacciones;
}
