package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.*;
import org.openxava.annotations.*;
import javax.persistence.*;
import java.util.Collection;

@Entity @Getter @Setter
public class Organizacion {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Required
    @Column(length = 80)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "organizacion")
    private Collection<ni.edu.uamv.proyecto_BillManager.modelo.Usuario> usuarios;
}
