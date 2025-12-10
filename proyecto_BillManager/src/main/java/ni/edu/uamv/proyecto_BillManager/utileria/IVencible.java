package ni.edu.uamv.proyecto_BillManager.utileria;

import java.time.LocalDate;

public interface IVencible {

    // Contrato: Las clases que usen esto deben tener estos métodos (Getters)
    LocalDate getFechaVencimiento();
    boolean isPagado();

    // Lógica por defecto (Default Method de Java 8+)
    // Así la lógica vive en la interfaz y no se repite en las clases
    default boolean calcularSiEstaVencida() {
        // Si ya está pagado, no está vencida.
        if (isPagado()) return false;

        // Si no tiene fecha de vencimiento, no vence.
        if (getFechaVencimiento() == null) return false;

        // Está vencida si hoy es después de la fecha límite
        return LocalDate.now().isAfter(getFechaVencimiento());
    }
}