package ni.edu.uamv.proyecto_BillManager.modelo;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

import java.text.DecimalFormat;
import java.util.*;
import lombok.*;

import javax.persistence.Table;

@Entity @Getter @Setter
@Tab(properties = "fecha, usuario.nombre, cuenta.nombre, categoria.nombre, monto, descripcion")
public class Transaccion {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @ManyToOne(optional = false)
    private Cuenta cuenta;

    @ManyToOne(optional = false)
    private Categoria categoria;

    private java.time.LocalDate fecha;

    private double monto;

    @Column(length = 120)
    private String descripcion;

    @Column(length = 255)
    private String adjuntoUrl;

    private java.time.LocalDateTime creadoEn;
}
