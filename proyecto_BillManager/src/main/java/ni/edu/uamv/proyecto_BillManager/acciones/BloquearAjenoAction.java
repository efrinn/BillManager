package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.Map;
import org.openxava.actions.SearchByViewKeyAction;
import org.openxava.model.MapFacade;
import org.openxava.util.Users;
import org.apache.commons.beanutils.PropertyUtils;

public class BloquearAjenoAction extends SearchByViewKeyAction {

    @Override
    public void execute() throws Exception {
        // 1. Carga los datos visuales primero
        super.execute();

        try {
            // 2. Obtiene la entidad real desde la base de datos
            Map key = getView().getKeyValues();
            String modelo = getView().getModelName();
            Object entity = MapFacade.findEntity(modelo, key);

            if (entity != null) {
                // 3. Obtiene quién es el dueño y quién está logueado
                String duenoRegistro = (String) PropertyUtils.getSimpleProperty(entity, "usuario");
                String usuarioLogueado = Users.getCurrent();

                // 4. Lógica de Seguridad Estricta
                // Si el dueño es nulo (registro viejo) O el dueño no soy yo -> BLOQUEAR
                if (duenoRegistro == null || !duenoRegistro.equals(usuarioLogueado)) {

                    // Deshabilitar edición
                    getView().setEditable(false);

                    // Eliminar botones peligrosos
                    removeActions("CRUD.save", "CRUD.delete", "CRUD.new");

                    // Mensaje claro
                    if (duenoRegistro == null) {
                        addWarning("Modo lectura: Este registro no tiene propietario asignado (dato antiguo).");
                    } else {
                        addWarning("Modo lectura: Este registro pertenece a " + duenoRegistro);
                    }

                } else {
                    // 5. Si es mi registro -> PERMITIR
                    getView().setEditable(true);
                    addActions("CRUD.save", "CRUD.delete", "CRUD.new");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Si falla algo, bloqueamos por seguridad
            getView().setEditable(false);
            addError("Error verificando permisos de seguridad.");
        }
    }
}