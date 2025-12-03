package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.*;
import java.math.BigDecimal;
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


        String desc = (transaccion.getDescripcion() != null) ? transaccion.getDescripcion() : "Sin descripción";
        parameters.put("descripcion", desc);
        parameters.put("fechaTransaccion", (transaccion.getFechaTransaccion() != null) ? java.sql.Date.valueOf(transaccion.getFechaTransaccion()) : new Date());
        parameters.put("fechaImpresion", new Date());
        parameters.put("cuenta", "Cuenta Principal");
        parameters.put("numeroHoja", "1");
        parameters.put("monto", transaccion.getMonto());

        // --- LÓGICA DE 3 COLUMNAS: ACTUAL | META | DIFERENCIA ---
        String tipo = "TRANSACCIÓN";
        String origen = "General";


        BigDecimal colActual = null;     //  Cuanto llevo
        String lblActual = "";

        BigDecimal colMeta = null;       // Cual es el tope
        String lblMeta = "";

        BigDecimal colDiferencia = null; //  Cuanto falta/sobra
        String lblDiferencia = "";

        if (transaccion.getPresupuesto() != null) {
            // --- LOGICA PRESUPUESTO ---
            tipo = "GASTO";
            origen = "Presupuesto: " + transaccion.getPresupuesto().getNombre();

            BigDecimal limite = transaccion.getPresupuesto().getMontoLimite();
            BigDecimal gastado = transaccion.getPresupuesto().getGastoTotal();

            //  Actual
            colActual = gastado;
            lblActual = "Total Gastado:";

            //  Meta (Límite)
            colMeta = limite;
            lblMeta = "Límite Mensual:";

            // Diferencia Disponible Límite  Gastado
            colDiferencia = limite.subtract(gastado);
            lblDiferencia = "Saldo Disponible:";

        } else if (transaccion.getMeta() != null) {

            tipo = "AHORRO / META";
            origen = "Meta: " + transaccion.getMeta().getNombre();

            BigDecimal objetivo = transaccion.getMeta().getMontoObjetivo();
            BigDecimal acumulado = transaccion.getMeta().getMontoAcumulado();

            // Actual
            colActual = acumulado;
            lblActual = "Total Ahorrado:";

            //  Meta (Objetivo)
            colMeta = objetivo;
            lblMeta = "Objetivo Meta:";


            colDiferencia = objetivo.subtract(acumulado);

            // Si ya cumplió la meta
            if (colDiferencia.compareTo(BigDecimal.ZERO) <= 0) {
                lblDiferencia = "¡Meta Cumplida! (Sobra):";
                colDiferencia = colDiferencia.abs(); // Mostramos positivo cuanto sobra
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