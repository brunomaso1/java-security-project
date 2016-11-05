package obligatorioseguridad;

import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Usuario {
    private String nombre;
    private String usuario;
    private String password;
    
    public Retorno altaUsuario(String nombre, String usuario, String password, String password2) {
        Retorno retorno = new Retorno();
        if (password.length() != 16 || password2.length() != 16) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El largo de la constraseña debe ser de 16 caracteres");
        } else if (!password.equals(password2)) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("Las constraseñas no coinciden");
        } else if (usuario.length() == 0) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El usuario no puede ser vacío");
        } else if (nombre.length() == 0) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El nombre no puede ser vacío");
        } else {
            try {
                String passwordHasheada = hashToPassword(password);
                Conexion c = new Conexion();
                
                // Verifico que no exista el usuario en la base antes de insertarlo
                boolean yaExiste = consultaUsuario(usuario);
                
                if (yaExiste) {
                    retorno.setCodigo(-2);
                    retorno.setDescripcion("El usuario " + usuario + " ya existe");
                } else {
                    Statement stmt = c.getConnection().createStatement();
                    String sql = "INSERT INTO usuario " +
                          "(nombre, nombre_usuario, password)"
                            + " VALUES ('" + nombre
                            + "', '" + usuario 
                            + "', '" + passwordHasheada + "')";
                    stmt.executeUpdate(sql);
                    stmt.close();
                    retorno.setCodigo(0);
                    retorno.setDescripcion("Éxito en la operación");
                }
            } catch (SQLException e) {
                retorno.setCodigo(-3);
                retorno.setDescripcion("Error en consulta SQL");
            }
        }
        return retorno;
    }
    
    private boolean consultaUsuario (String usuario) {
        boolean retorno = false;
        try {
            Conexion c = new Conexion();
            Statement stmt = c.getConnection().createStatement();
            String sql = "SELECT id_usuario FROM usuario WHERE nombre_usuario = '" +
                usuario + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                retorno = true;
            }
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return retorno;
    }
    
    public Retorno eliminarUsuario (String usuario) {
        Retorno retorno = new Retorno();
        
        if (usuario.length() == 0) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El usuario no puede ser vacío");
        } else {
            try {
                                
                // Verifico que exista el usuario en la base antes de borrarlo
                boolean yaExiste = consultaUsuario(usuario);
                
                if (!yaExiste) {
                    retorno.setCodigo(-4);
                    retorno.setDescripcion("El usuario " + usuario + " no existe");
                } else {                
                    Conexion c = new Conexion();
                    Statement stmt = c.getConnection().createStatement();

                    String sql = "DELETE FROM usuario WHERE nombre_usuario = '" +
                          usuario + "'";
                    stmt.executeUpdate(sql);
                    stmt.close();
                    retorno.setCodigo(0);
                    retorno.setDescripcion("Éxito en la operación");
                }
            } catch (SQLException e) {
                retorno.setCodigo(-3);
                retorno.setDescripcion("Error en consulta SQL");
            }
        }
        
        return retorno;
    }
    
    public List<String> listarUsuarios () {
        List<String> listaUsuarios = new ArrayList<>();

        try {                       
            Conexion c = new Conexion();
            Statement stmt = c.getConnection().createStatement();

            String sql = "SELECT nombre_usuario FROM usuario";
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                do {
                    listaUsuarios.add(rs.getString("nombre_usuario"));
                } while (rs.next());
            }
            
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return listaUsuarios;
    }
    
    public List<Permiso> listarPermisosUsuario (String usuario) {
        List<Permiso> listaPermisos = new ArrayList<>();

        try {            
            Conexion c = new Conexion();
            Statement stmt = c.getConnection().createStatement();

            String sql = "SELECT p.id_permiso, p.descripcion FROM permiso p"
                    + " JOIN permiso_usuario pu"
                    + " ON p.id_permiso = pu.id_permiso"
                    + " JOIN usuario u"
                    + " ON pu.id_usuario = u.id_usuario"
                    + " WHERE u.nombre_usuario = '" + usuario + "'";
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                do {
                    Permiso p = new Permiso();
                    p.setIdPermiso(rs.getInt("p.id_permiso"));
                    p.setDescripcion(rs.getString("p.descripcion"));
                    listaPermisos.add(p);
                } while (rs.next());
            }
            
            stmt.close();
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return listaPermisos;
    } 
    
    public String hashToPassword (String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] result = md.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
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
