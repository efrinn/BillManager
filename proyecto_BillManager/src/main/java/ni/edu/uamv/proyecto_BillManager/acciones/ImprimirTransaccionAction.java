package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.*;
import org.openxava.actions.*;
import org.openxava.model.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import ni.edu.uamv.proyecto_BillManager.modelo.Transaccion;

public class ImprimirTransaccionAction extends JasperReportBaseAction {

    // 1. Decimos de dónde salen los DATOS (Este es el método que te faltaba)
    @Override
    protected JRDataSource getDataSource() throws Exception {
        // Obtenemos el mapa de claves (IDs)
        Map key = getView().getKeyValues();

        // --- CORRECCIÓN IMPORTANTE ---
        // Verificamos específicamente si el valor de "oid" es nulo
        if (key == null || key.get("oid") == null) {
            // Esto muestra un mensaje rojo bonito en la pantalla en lugar del error técnico
            addError("¡Alto ahí! Primero debes GUARDAR la transacción antes de imprimirla.");
            return new JREmptyDataSource(); // Retorna vacío para no romper el programa
        }
        // -----------------------------

        // Buscamos la entidad usando el mapa de claves
        Transaccion t = (Transaccion) MapFacade.findEntity("Transaccion", key);

        // Validación extra por si se borró mientras la veías
        if (t == null) {
            addError("No se encontró la transacción en la base de datos.");
            return new JREmptyDataSource();
        }

        List<Transaccion> datos = new ArrayList<>();
        datos.add(t);

        return new JRBeanCollectionDataSource(datos);
    }

    // 2. Decimos cuál es el ARCHIVO de de la lista
    @Override
    protected String getJRXML() throws Exception {
        return "reports/TransaccionReport.jrxml";
    }

    // 3. Parámetros extra (obligatorio declararlo)
    //para este caso no usamos ninguno así que devolvemos null
    @Override
    protected Map getParameters() throws Exception {
        return null;
    }
}