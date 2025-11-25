package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
public class Categoria {

    // OBLIGATORIO: Define la lógica matemática
    public enum Tipo { GASTO, INGRESO }

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(length = 40, nullable = false)
    @Required
    private String nombre; // Ej: Ventas, Comida

    @Column(length = 2)
    private String icono; // Ej: ?, ?

    // CAMPO VITAL
    @Enumerated(EnumType.STRING)
    @Required
    private Tipo tipo; // Define si suma o resta

    @Column(length = 200)
    @Stereotype("MEMO")
    private String descripcion;
}