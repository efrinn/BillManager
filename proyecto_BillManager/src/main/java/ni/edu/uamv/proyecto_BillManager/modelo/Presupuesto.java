package ni.edu.uamv.proyecto_BillManager.modelo;

import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Month;
import java.time.Year;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*; // Para CurrentYearCalculator

@Entity
@Getter @Setter
@Tab(
        filter=FiltroUsuario.class,
        baseCondition="${usuario.nombre} = ?", // El filtro inyecta el usuario aquí
        properties="periodo, anio, categoria.nombre, montoLimite"
)
@View(members="usuario; periodo, anio; categoria; montoLimite, notas")
public class Presupuesto {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    // Vinculamos el presupuesto al Usuario automáticamente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @DefaultValueCalculator(value=CurrentUserCalculator.class) // Se llena solo
    @ReadOnly // No se toca
    private Persona usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @DescriptionsList(descriptionProperties = "icono, nombre", showReferenceView=true)
    @Required(message = "Debes elegir una categoría")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    @Required
    private Month periodo; // Enero, Febrero...

    @Column(length = 4)
    @Required
    @DefaultValueCalculator(CurrentYearCalculator.class) // Pone 2025 solo
    private int anio;

    @Column(name = "monto_limite")
    @Stereotype("MONEY")
    @Required(message = "Define cuánto quieres gastar máximo")
    private BigDecimal montoLimite;

    @Column(length = 100)
    @Stereotype("MEMO")
    private String notas;
}