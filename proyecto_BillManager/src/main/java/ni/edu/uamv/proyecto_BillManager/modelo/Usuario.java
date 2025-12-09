package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import org.openxava.annotations.*;

// [SOLUCIÓN] Esta es la línea que faltaba:
import javax.validation.constraints.AssertTrue;

@Entity
@Getter @Setter
// La vista para el registro
@View(members="nombre, apellido; nombreUsuario; contrasena; confirmarContrasena; foto")
public class Usuario {

    @Id
    @Column(name="user_name", length=30)
    @Required(message="El usuario es obligatorio")
    private String nombreUsuario;

    @Column(length=60)
    @Required
    private String nombre;

    @Column(length=60)
    @Required
    private String apellido;

    // Guardamos la contraseña aquí (OJO: En texto plano según lógica CodeCash)
    @Column(length=20)
    @Stereotype("PASSWORD")
    @Hidden // Se oculta en las listas, solo visible al editar
    private String contrasena;

    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Stereotype("PHOTO")
    private byte[] foto;

    // --- CAMPOS PARA EL FORMULARIO (NO SE GUARDAN EN BD) ---

    @Transient // No se guarda en base de datos, solo sirve para cambiar el pass
    @Stereotype("PASSWORD")
    private String confirmarContrasena;

    // Validación de coincidencia (Lógica de CodeCash adaptada)
    @AssertTrue(message = "Las contraseñas no coinciden")
    private boolean isCoinciden() {
        if (contrasena == null || contrasena.isEmpty()) return false;
        if (confirmarContrasena == null) return true; // Si no la está cambiando/confirmando
        return contrasena.equals(confirmarContrasena);
    }
}