package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.Min;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentDateCalculator;
import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;

@Entity
@Getter @Setter
@Tab(
        filter=FiltroUsuario.class,
        baseCondition="${usuarioAutor.nombre} = ?",
        properties="fechaTransaccion, nombre, categoria.nombre, negocio.nombre, monto",
        defaultOrder="fechaTransaccion desc"
)
@View(members=
        "Principal { fechaTransaccion; nombre; monto } " +
                "Clasificacion { categoria; negocio } " +
                "Detalles { usuarioAutor; descripcion; comprobante }"
)
@View(name="Simple", members="nombre, categoria, monto, fechaTransaccion")
public class Transaccion {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="usuario_id", nullable=false)
    @DescriptionsList(descriptionProperties="nombre")
    @DefaultValueCalculator(org.openxava.calculators.CurrentUserCalculator.class)
    @Required
    private Persona usuarioAutor;

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Stereotype("MEMO")
    private String descripcion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="categoria_id")
    @DescriptionsList(descriptionProperties="icono, nombre")
    private Categoria categoria;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="negocio_id")
    @DescriptionsList(descriptionProperties="nombre")
    private Negocio negocio;

    @Required
    @Stereotype("MONEY")
    @Min(0)
    private BigDecimal monto;

    @Required
    @DefaultValueCalculator(CurrentDateCalculator.class)
    private LocalDate fechaTransaccion;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] comprobante;
}
