package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
public class Cuenta {

    private enum tipo {ACTIVO, PASIVO, CAPITAL}

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(name = "nombre_cuenta", length = 60, nullable = false)
    @Required(message = "La cuenta tiene que tener un nombre")
    private String nombre;

    @Column(nullable = false)
    private tipo tipo;

    // Los saldos están escondidos a la hora de crear una cuenta
    // Esto es debido a que los saldos, son derivados de las transacciones

    @Column(name = "saldo_deudor", nullable = false)
    @Hidden
    private double saldoDeudor = 0;

    @Column(name = "saldo_acreedor", nullable = false)
    @Hidden
    private double saldoAcreedor = 0;
}
