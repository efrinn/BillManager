package ni.edu.uamv.proyecto_BillManager.acciones;

import com.openxava.naviox.actions.ForwardToOriginalURIBaseAction;
import com.openxava.naviox.impl.SignInHelper;
import org.openxava.jpa.XPersistence;
import org.openxava.util.Is;
import javax.persistence.Query;

public class SignInAction extends ForwardToOriginalURIBaseAction {

    public void execute() throws Exception {
        SignInHelper.initRequest(getRequest(), getView());

        String userName = getView().getValueString("user");
        String password = getView().getValueString("password");

        if (Is.emptyString(userName, password)) {
            addError("Usuario o contraseña vacíos");
            return;
        }

        // --- SOLUCIÓN: PERMITIR ACCESO AL ADMIN DE EMERGENCIA ---
        // Esto permite que 'admin' entre aunque no esté en tu tabla Usuario
        if ("admin".equals(userName) && "admin".equals(password)) {
            SignInHelper.signIn(getRequest(), userName);
            getView().reset();
            getContext().resetAllModulesExceptCurrent(getRequest());
            forwardToOriginalURI();
            return; // Importante: Salir para no ejecutar la consulta SQL
        }
        // --------------------------------------------------------

        // LÓGICA DE TU TABLA USUARIO
        String jpql = "SELECT count(u) FROM Usuario u WHERE u.nombreUsuario = :user AND u.contrasena = :pass";
        Query query = XPersistence.getManager().createQuery(jpql);
        query.setParameter("user", userName);
        query.setParameter("pass", password);

        Long count = (Long) query.getSingleResult();

        if (count == 0L) {
            addError("Usuario o contraseña incorrectos");
            return;
        }

        SignInHelper.signIn(getRequest(), userName);
        getView().reset();
        getContext().resetAllModulesExceptCurrent(getRequest());
        forwardToOriginalURI();
    }
}