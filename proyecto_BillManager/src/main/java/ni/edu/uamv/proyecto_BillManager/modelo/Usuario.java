package ni.edu.uamv.proyecto_BillManager.modelo;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import java.util.*;
import lombok.*;

import javax.persistence.Table;

@Entity

@Getter
@Setter
@View ( name = "Simple ",
        members= "numero;" + "nombre;" + "email;" + "fechaNacimiento")

public class Usuario {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

    @Column (name = "Nombre_usuario", length = 100, nullable = false)
    @Required(message = "El usuario debe tener nombre")
    private String nombre;

    @Column (name ="Correo", length = 100, nullable = false)
    @Required(message = "El usuario debe tener un correo")
    private String email;

    @Column(name = "passwords", length = 100, nullable = false)
    @Required(message = "El usuario debe tener una contraseña")
    private String password;

    @Temporal(TemporalType.DATE)
    private Date fechaRegistro;
}