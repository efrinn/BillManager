package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class Persona {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(length = 60, name = "nombre_usuario", nullable = false)
    @Required(message = "Todo usuario debe tener un nombre")
    private String nombre;

    @Column(length = 60, name = "pass", nullable = false)
    @Required(message = "Todo usuario debe tener una contraseña")
    private String password;

    @Column(length = 60, name = "email_usuario", nullable = false)
    private String email;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDate fechaRegistro = LocalDate.now();
}

