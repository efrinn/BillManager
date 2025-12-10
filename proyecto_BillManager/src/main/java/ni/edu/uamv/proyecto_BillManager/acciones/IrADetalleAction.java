package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.Map;
import org.openxava.actions.ViewDetailAction;
import org.openxava.model.MapFacade;
import org.openxava.util.Users;
import org.apache.commons.beanutils.PropertyUtils;

public class IrADetalleAction extends ViewDetailAction {

    @Override
    public void execute() throws Exception {
        super.execute(); // Carga la vista

        try {
            Map key = getView().getKeyValues();
            Object entity = MapFacade.findEntity(getView().getModelName(), key);

            if (entity != null) {
                // Obtenemos quién creó el registro
                String duenoRegistro = (String) PropertyUtils.getSimpleProperty(entity, "usuario");
                String usuarioLogueado = Users.getCurrent();

                // SI TIENE DUEÑO Y NO ERES TÚ -> BLOQUEAR
                if (duenoRegistro != null && !duenoRegistro.equals(usuarioLogueado)) {

                    // 1. Poner todo en gris (Solo lectura)
                    getView().setEditable(false);

                    // 2. ELIMINAR LOS BOTONES DE EDICIÓN
                    removeActions("CRUD.save", "CRUD.delete", "CRUD.new", "CRUD.refresh");

                    // 3. Aviso visual
                    addWarning("Modo lectura: Este registro pertenece a " + duenoRegistro);
                } else {
                    // Si es tuyo, todo normal
                    getView().setEditable(true);
                    addActions("CRUD.save", "CRUD.delete", "CRUD.new");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            getView().setEditable(true); // En caso de error, no bloqueamos para no romper la app
        }
    }
}