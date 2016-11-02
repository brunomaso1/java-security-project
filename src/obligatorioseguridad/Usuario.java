package obligatorioseguridad;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Statement;

public class Usuario {
    private String nombre;
    private String usuario;
    private String password;
    
    public int altaUsuario(String nombre, String usuario, String password, String password2) {
        int retorno;
        if (password.length() != 16 || password2.length() != 16) {
            retorno = -1;
        } else if (!password.equals(password2)) {
            retorno = -1;
        } else if (usuario.length() == 0) {
            retorno = -1;
        } else if (nombre.length() == 0) {
            retorno = -1;
        } else {
            try {
                String passwordHasheada = hashToPassword(password);
                Conexion c = new Conexion();
                Statement stmt = c.getConnection().createStatement();
                String sql = "INSERT INTO usuario " +
                      "(nombre, usuario, password)"
                        + " VALUES ('" + nombre
                        + "', '" + usuario 
                        + "', '" + passwordHasheada + "')";
                stmt.executeUpdate(sql);
                stmt.close();
                retorno = 0;
            } catch (SQLException e) {
                System.out.println("SQLException " + e);
                retorno = -3;
            }
        }
        return retorno;
    }
    
    public String hashToPassword (String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch(NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException: " + e);
            return null;
        }        
    }
    
    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
