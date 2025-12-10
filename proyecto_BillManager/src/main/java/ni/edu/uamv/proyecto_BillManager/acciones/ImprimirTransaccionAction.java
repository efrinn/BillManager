package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter; // Necesario para fechas si usas LocalDate
import org.openxava.actions.*;
import org.openxava.model.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import ni.edu.uamv.proyecto_BillManager.modelo.*;

public class ImprimirTransaccionAction extends JasperReportBaseAction {

    private Transaccion transaccion;

    @Override
    public void execute() throws Exception {
        Map key = (Map) getView().getKeyValues();
        if (key == null || key.get("oid") == null) {
            addError("Primero guarda la transacción.");
            return;
        }
        this.transaccion = (Transaccion) MapFacade.findEntity("Transaccion", key);
        if (this.transaccion == null) {
            addError("No se encontró la transacción.");
            return;
        }
        super.execute();
    }

    @Override
    protected JRDataSource getDataSource() throws Exception {
        return new JRBeanArrayDataSource(new Object[] { transaccion });
    }

    @Override
    protected String getJRXML() throws Exception {
        return "reports/TransaccionReport.jrxml";
    }

    @Override
    protected Map getParameters() throws Exception {
        Map<String, Object> parameters = new HashMap<>();

        // Datos básicos
        String desc = (transaccion.getDescripcion() != null) ? transaccion.getDescripcion() : "Sin descripción";
        parameters.put("descripcion", desc);

        // Conversión segura de LocalDate a Date para JasperReports
        Date fechaTx = (transaccion.getFechaTransaccion() != null)
                ? java.sql.Date.valueOf(transaccion.getFechaTransaccion())
                : new Date();
        parameters.put("fechaTransaccion", fechaTx);

        parameters.put("fechaImpresion", new Date());
        parameters.put("cuenta", "Usuario: " + transaccion.getUsuario()); // Mostramos el usuario
        parameters.put("numeroHoja", "1");
        parameters.put("monto", transaccion.getMonto());

        // --- NUEVOS CAMPOS ---
        // 1. Categoría
        String catNombre = "General";
        if (transaccion.getCategoria() != null) {
            catNombre = transaccion.getCategoria().getNombre();
        }
        parameters.put("categoria", catNombre);

        // 2. Método de Pago
        String metodo = "No especificado";
        if (transaccion.getTipoPago() != null) {
            metodo = transaccion.getTipoPago().toString();
        }
        parameters.put("metodoPago", metodo);

        // 3. Estado (Pagado / Pendiente / Vencido)
        String estado = "PENDIENTE";
        if (transaccion.isPagado()) {
            estado = "PAGADO";
        } else if (transaccion.isVencida()) {
            estado = "VENCIDO";
        }
        parameters.put("estado", estado);


        // --- LÓGICA DE 3 COLUMNAS: ACTUAL | META | DIFERENCIA ---
        String tipo = "TRANSACCIÓN";
        String origen = "Movimiento Suelto"; // Valor por defecto

        BigDecimal colActual = null;
        String lblActual = "";

        BigDecimal colMeta = null;
        String lblMeta = "";

        BigDecimal colDiferencia = null;
        String lblDiferencia = "";

        if (transaccion.getPresupuesto() != null) {
            // --- LOGICA PRESUPUESTO ---
            tipo = "GASTO";
            origen = "Presupuesto: " + transaccion.getPresupuesto().getNombre();

            BigDecimal limite = transaccion.getPresupuesto().getMontoLimite();
            BigDecimal gastado = transaccion.getPresupuesto().getGastoTotal();

            colActual = gastado;
            lblActual = "Total Gastado:";

            colMeta = limite;
            lblMeta = "Límite Mensual:";

            colDiferencia = limite.subtract(gastado);
            lblDiferencia = "Saldo Disponible:";

        } else if (transaccion.getMeta() != null) {
            // --- LOGICA META ---
            tipo = "AHORRO / META";
            origen = "Meta: " + transaccion.getMeta().getNombre();

            BigDecimal objetivo = transaccion.getMeta().getMontoObjetivo();
            BigDecimal acumulado = transaccion.getMeta().getMontoAcumulado();

            colActual = acumulado;
            lblActual = "Total Ahorrado:";

            colMeta = objetivo;
            lblMeta = "Objetivo Meta:";

            colDiferencia = objetivo.subtract(acumulado);

            if (colDiferencia.compareTo(BigDecimal.ZERO) <= 0) {
                lblDiferencia = "¡Meta Cumplida! (Sobra):";
                colDiferencia = colDiferencia.abs();
            } else {
                lblDiferencia = "Falta para Meta:";
            }
        }

        parameters.put("tipo", tipo);
        parameters.put("origen", origen);

        parameters.put("colActual", colActual);
        parameters.put("lblActual", lblActual);

        parameters.put("colMeta", colMeta);
        parameters.put("lblMeta", lblMeta);

        parameters.put("colDiferencia", colDiferencia);
        parameters.put("lblDiferencia", lblDiferencia);

        return parameters;
    }
}