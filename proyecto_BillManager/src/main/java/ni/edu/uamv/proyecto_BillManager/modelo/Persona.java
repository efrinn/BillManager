package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Email;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.CurrentDateCalculator;
import java.time.LocalDate;
import java.util.Collection;

@Entity
@Getter @Setter
@Tab(
        properties="nombre, email, telefono, tipo, monedaPreferida, fechaRegistro",
        defaultOrder="nombre asc"
)
@View(members=
        "Perfil { foto; tipo; nombre; email; telefono; monedaPreferida; } " +
                "Seguridad { password; fechaRegistro } " +
                "Negocios { negocios } " +
                "Transacciones { transacciones }"
)
@View(name="Simple", members="nombre")
public class Persona {

    public enum Moneda { CORDOBAS, DOLARES }
    public enum Tipo { PERSONAL, EMPRESA }

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] foto;

    @Enumerated(EnumType.STRING)
    @Column(length=20, nullable=false)
    @Required
    private Tipo tipo;

    @Column(length=60, nullable=false)
    @Required
    private String nombre;

    @Email
    @Required
    @Column(length=60, nullable=false)
    @Stereotype("EMAIL")
    private String email;

    @Column(length=20)
    @Stereotype("TELEPHONE")
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Required
    @Column(length=10)
    private Moneda monedaPreferida;

    @Required
    @Column(length=60, nullable=false)
    @Stereotype("PASSWORD")
    private String password;

    @ReadOnly
    @Column(name="fecha_registro", nullable=false)
    @DefaultValueCalculator(CurrentDateCalculator.class)
    private LocalDate fechaRegistro = LocalDate.now();

    @OneToMany(mappedBy="usuarioAutor")
    @CollectionView("Simple")
    private Collection<Transaccion> transacciones;

    @OneToMany(mappedBy="usuarioPropietario")
    private Collection<Negocio> negocios;

    @PrePersist
    public void prePersist() {
        if (fechaRegistro == null) fechaRegistro = LocalDate.now();
        if (monedaPreferida == null) monedaPreferida = Moneda.CORDOBAS;
        if (tipo == null) tipo = Tipo.PERSONAL;
    }
}
