/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package returns;

import main.Usuario;

/**
 * Esta clase se encagra de administrar los mensajes de retorno.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class RetornoUsuario extends Retorno {
    private Usuario usuario;

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
