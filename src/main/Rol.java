/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package main;

/**
 * Esta clase se encarga de administrar los roles.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class Rol {
    private int idRol;
    private String descripcion;

    /**
     * @return the idRol
     */
    public int getIdRol() {
        return idRol;
    }

    /**
     * @param idRol the idRol to set
     */
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    /**
     * @return the descripcion
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion the descripcion to set
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
