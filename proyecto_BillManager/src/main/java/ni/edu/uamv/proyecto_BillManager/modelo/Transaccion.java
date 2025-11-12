package ni.edu.uamv.proyecto_BillManager.modelo;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import java.util.*;
import lombok.*;

import javax.persistence.Table;

@Entity
@Table(name = "Transacciones")
@Getter @Setter
public class Transaccion {

    @Id
    @Hidden
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;
}
