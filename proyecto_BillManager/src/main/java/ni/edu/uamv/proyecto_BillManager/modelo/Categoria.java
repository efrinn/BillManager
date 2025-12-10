package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.*;
import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
public class Categoria {

    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Column(length=50)
    @Required(message="La categoría debe tener un nombre")
    private String nombre;

    @Column(length=30)
    @Stereotype("ICON") // Opcional: si quieres guardar un emoji o ícono
    private String icono;

}