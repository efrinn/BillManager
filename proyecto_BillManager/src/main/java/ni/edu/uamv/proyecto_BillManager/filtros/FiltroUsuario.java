package ni.edu.uamv.proyecto_BillManager.filtros;

import org.openxava.filters.*;
import org.openxava.util.*;

// Esta clase obtiene el usuario conectado para filtrar los datos
public class FiltroUsuario implements IFilter {

    public Object filter(Object o) throws FilterException {

        return new Object [] { Users.getCurrent() };
    }

}