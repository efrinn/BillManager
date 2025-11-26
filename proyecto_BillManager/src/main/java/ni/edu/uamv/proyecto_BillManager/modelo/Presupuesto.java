package ni.edu.uamv.proyecto_BillManager.modelo;

import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;
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
        filter=FiltroUsuario.class,
        baseCondition="${usuario} = ?", // Filtro rápido por String
        properties="periodo, anio, categoria.nombre, montoLimite"
)
@View(members="periodo, anio; categoria; montoLimite, notas")
public class Presupuesto {

    @Id @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    // --- SEGURIDAD ---
    @Column(length=50)
    @Hidden
    @DefaultValueCalculator(CurrentUserCalculator.class)
    private String usuario;
    // -----------------

    // Opcional: Relación con el Perfil completo si necesitas datos extra (como moneda)
    // Pero NO la usamos para filtrar la lista principal.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id")
    @ReferenceView("Simple")
    @ReadOnly
    private Persona perfilUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    @DescriptionsList(descriptionProperties = "icono, nombre", showReferenceView=true)
    @Required(message = "Debes elegir una categoría")
    private Categoria categoria;

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

    @PrePersist @PreUpdate
    private void asegurarUsuario() {
        if (this.usuario == null) this.usuario = org.openxava.util.Users.getCurrent();
    }
}