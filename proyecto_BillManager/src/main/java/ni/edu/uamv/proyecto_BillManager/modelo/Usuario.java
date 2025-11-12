package ni.edu.uamv.proyecto_BillManager.modelo;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import java.util.*;
import lombok.*;

import javax.persistence.Table;

@View ( name = "Simple ",
        members= "numero;" + "nombre;" + "email;" + "fechaNacimiento")

@Entity @Getter @Setter
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Required @Column(length = 60)
    private String nombre;

    @Required @Column(length = 80)
    private String email;

    @Hidden
    private String passwordHash;

    private java.time.LocalDateTime fechaRegistro;

    private boolean sesionActiva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Organizacion organizacion;
}

