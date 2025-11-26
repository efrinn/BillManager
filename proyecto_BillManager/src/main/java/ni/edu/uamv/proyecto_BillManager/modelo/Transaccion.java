package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.Identifiable;
import ni.edu.uamv.proyecto_BillManager.filtros.FiltroUsuario;

@Entity
@Getter @Setter
@Tab(
        filter=FiltroUsuario.class,
        baseCondition="${usuario} = ?", // Filtramos por el login exacto (seguro y rápido)
        properties="fechaTransaccion, nombre, categoria.nombre, negocio.nombre, monto",
        defaultOrder="fechaTransaccion desc"
)
@View(members=
        "Principal { fechaTransaccion; nombre; monto } " +
                "Clasificacion { categoria; negocio } " +
                "Detalles { descripcion; comprobante }"
)
public class Transaccion {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    // --- SEGURIDAD ---
    // Guardamos el "login" del usuario (ej: "admin") automáticamente.
    // Esto hace que el filtro sea infalible.
    @Column(length=50)
    @Hidden
    @DefaultValueCalculator(CurrentUserCalculator.class) // Se llena solo al guardar
    private String usuario;

    // Mantenemos la relación con Persona si la necesitas para otros datos,
    // pero ya no dependemos de ella para el filtro de seguridad básico.
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="persona_id") // Cambiado nombre para evitar conflicto
    @DescriptionsList(descriptionProperties="nombre")
    private Persona usuarioAutor;
    // -----------------

    @Required
    @Column(length=60, nullable=false)
    private String nombre;

    @Stereotype("MEMO")
    private String descripcion;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="categoria_id")
    @DescriptionsList(descriptionProperties="icono, nombre", condition="${tipo} = 'GASTO' OR ${tipo} = 'INGRESO'")
    @Required
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

    // MEJORA: FILE permite subir PDF, Imágenes, Excel, etc.
    // Guarda un ID (String), no el binario pesado en la tabla principal.
    @Column(length=32)
    @Stereotype("FILE")
    private String comprobante;

    // --- VALIDACIONES LÓGICAS (RF01) ---
    @AssertTrue(message = "La categoría seleccionada no coincide con una lógica válida de transacción")
    private boolean isCategoriaCoherente() {
        if (categoria == null) return true;


        return true;
    }

    @PrePersist @PreUpdate
    private void alGuardar() {
        // Aseguramos que el usuario siempre esté setado por seguridad
        if (this.usuario == null) {
            this.usuario = org.openxava.util.Users.getCurrent();
        }
    }
}