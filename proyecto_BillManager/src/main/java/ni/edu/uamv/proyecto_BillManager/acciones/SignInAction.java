package ni.edu.uamv.proyecto_BillManager.acciones;

import com.openxava.naviox.actions.ForwardToOriginalURIBaseAction;
import com.openxava.naviox.impl.SignInHelper; // Necesario para crear la sesión
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;
import javax.persistence.Query;
import ni.edu.uamv.proyecto_BillManager.modelo.Usuario; // Tu clase Usuario

public class SignInAction extends ForwardToOriginalURIBaseAction {

    public void execute() throws Exception {
        // Inicializa la petición
        SignInHelper.initRequest(getRequest(), getView());

        // Obtiene lo que el usuario escribió en la pantalla de login
        String userName = getView().getValueString("user");
        String password = getView().getValueString("password");

        // Validación básica
        if (Is.emptyString(userName, password)) {
            addError("Usuario o contraseña vacíos");
            return;
        }

        // --- LÓGICA CODECASH: Buscar en TU tabla Usuario ---
        // Verificamos si existe un Usuario con ese user Y esa contraseña
        String jpql = "SELECT count(u) FROM Usuario u WHERE u.nombreUsuario = :user AND u.contrasena = :pass";
        Query query = XPersistence.getManager().createQuery(jpql);
        query.setParameter("user", userName);
        query.setParameter("pass", password); // Comparación directa (texto plano)

        Long count = (Long) query.getSingleResult();

        if (count == 0L) {
            // Si es 0, no existe o la contraseña está mal
            addError("Usuario o contraseña incorrectos");
            // Nota: "unauthorized_user" es la llave de mensaje original de CodeCash
            return;
        }

        // --- ÉXITO: Iniciar Sesión en NaviOX ---
        // Le decimos a OpenXava: "Confía en mí, loguea a este usuario"
        SignInHelper.signIn(getRequest(), userName);

        getView().reset();
        getContext().resetAllModulesExceptCurrent(getRequest());
        forwardToOriginalURI(); // Redirige a la aplicación
    }
}