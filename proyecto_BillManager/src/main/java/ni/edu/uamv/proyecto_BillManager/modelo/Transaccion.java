package ni.edu.uamv.proyecto_BillManager.modelo;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.*;
import javax.validation.constraints.*;

// [NUEVO] Imports corregidos según tu nueva estructura de paquetes
import ni.edu.uamv.proyecto_BillManager.utileria.IVencible;
import ni.edu.uamv.proyecto_BillManager.utileria.TipoPago;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.util.Users;

@Entity
@Getter @Setter
@Tab(
        // [¡CRÍTICO CORREGIDO!] El 'oid' (la llave primaria) debe ir primero para que el Tab funcione.
        // Aunque tiene @Hidden, debe estar en la lista.
        properties="oid, nombre, fechaTransaccion, monto, pagado, vencida, tipoPago, categoria.nombre",
        defaultOrder="fechaTransaccion desc"
)
@View(members=
        "Principal { " +
                "fechaTransaccion; " +
                "nombre; " +
                "monto; " +
                "} " +
                "Clasificacion { " +
                "categoria; " +
                "tipoPago; " +
                "presupuesto; " +
                "meta; " +
                "} " +
                "Estado { " +
                "pagado; " +
                "fechaVencimiento; " +
                "vencida; " +
                "} " +
                "Detalles { " +
                "descripcion; " +
                "comprobante; " +
                "}"
)
public class Transaccion implements IVencible {

    // El ID debe estar @Hidden para no verlo, pero debe estar en @Tab
    @Id @Hidden
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy="uuid2")
    private String oid;

    @Column(length = 50)
    @Hidden
    private String usuario;

    @PrePersist
    public void onPrePersist() {
        if (usuario == null) {
            usuario = Users.getCurrent();
        }
    }

    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList(descriptionProperties = "nombre")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private TipoPago tipoPago;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_presupuesto", nullable = true)
    private Presupuesto presupuesto;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "id_meta", nullable = true)
    private Meta meta;

    @Required(message = "La transacción debe llevar nombre")
    @Column(name = "nombre_transaccion", length=60, nullable=false)
    private String nombre;

    @Column(name = "desc_transaccion", length = 250)
    @Stereotype("MEMO")
    private String descripcion;

    @Column(name = "monto_transaccion", nullable = false)
    @Required(message = "La transacción debe tener un monto")
    @Stereotype("MONEY")
    @Min(value = 0, message = "El monto no puede ser negativo")
    private BigDecimal monto;

    @Column(name = "fecha_transaccion")
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class)
    @ReadOnly
    private LocalDate fechaTransaccion;

    // --- Gestión de Pagos y Vencimientos ---

    @Column(name = "esta_pagado")
    private boolean pagado;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Depends("pagado, fechaVencimiento")
    public boolean isVencida() {
        return IVencible.super.calcularSiEstaVencida();
    }

    @Column(name = "comprobante_transaccion", length=32)
    @Stereotype("FILE")
    private String comprobante;

    @AssertTrue(message = "La transacción debe pertenecer a un Presupuesto O a una Meta, pero no a ambos a la vez.")
    private boolean isDestinoUnico() {
        return (presupuesto != null && meta == null) ||
                (presupuesto == null && meta != null) ||
                (presupuesto == null && meta == null);
    }

    // [CRÍTICO] Método toString para evitar el error "Imposible convertir Monto..."
    @Override
    public String toString() {
        return nombre != null ? nombre : "Transacción " + oid;
    }
}