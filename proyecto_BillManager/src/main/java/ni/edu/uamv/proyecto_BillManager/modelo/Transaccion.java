package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

@Entity
@Getter @Setter
public class Transaccion {

    private enum tipo {ACTIVO, PASIVO, CAPITAL}

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String oid;

    @Column(length = 60, name = "nombre_transaccion", nullable = false)
    @Required(message = "La transaccion debe tener un nombre")
    private String nombre;

    @Column(name = "monto_transaccion", nullable = false)
    @Required(message = "Es obligatorio especificar un monto")
    private double monto;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cuenta cuentaDeudora;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cuenta cuentaAcreedora;
}
