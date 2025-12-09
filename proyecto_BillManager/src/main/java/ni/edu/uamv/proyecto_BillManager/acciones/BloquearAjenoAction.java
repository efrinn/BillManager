package ni.edu.uamv.proyecto_BillManager.acciones;

import java.util.Map;
import org.openxava.actions.SearchByViewKeyAction;
import org.openxava.model.MapFacade;
import org.openxava.util.Users;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Acción de seguridad: Carga el registro y verifica si el usuario conectado es el dueño.
 * Si no es el dueño, pone la vista en modo "Solo Lectura".
 */
public class BloquearAjenoAction extends SearchByViewKeyAction {

    @Override
    public void execute() throws Exception {
        // 1. Carga los datos en la pantalla (comportamiento normal)
        super.execute();

        try {
            // 2. Obtenemos la clave del objeto que acabamos de cargar
            Map key = getView().getKeyValues();

            // 3. Buscamos la entidad real en base de datos para leer el campo 'usuario'
            // (Usamos MapFacade porque 'usuario' es @Hidden y no está en la vista visual)
            Object entity = MapFacade.findEntity(getView().getModelName(), key);

            if (entity != null) {
                // Leemos la propiedad "usuario" usando reflexión (funciona para Meta, Presupuesto y Transaccion)
                String duenoRegistro = (String) PropertyUtils.getSimpleProperty(entity, "usuario");
                String usuarioLogueado = Users.getCurrent();

                // 4. Lógica de bloqueo
                // Si hay un dueño, no soy yo, y no soy admin -> BLOQUEAR
                if (duenoRegistro != null && !duenoRegistro.equals(usuarioLogueado) && !"admin".equals(usuarioLogueado)) {

                    getView().setEditable(false); // <--- Aquí ocurre la magia (deshabilita todo)
                    addWarning("Modo lectura: Este registro pertenece a " + duenoRegistro);

                } else {
                    // Si es mío o soy admin, aseguro que sea editable
                    getView().setEditable(true);
                }
            }
        } catch (Exception ex) {
            // En caso de error (ej. entidad sin campo usuario), dejamos editable por defecto para no bloquear el sistema
            ex.printStackTrace();
            getView().setEditable(true);
        }
    }
}