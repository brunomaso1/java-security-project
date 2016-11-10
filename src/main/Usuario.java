/*
 * Universidad Catolica - Seguridad - Obligatorio.
 */
package main;

import returns.Retorno;
import returns.RetornoUsuario;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Esta clase se encarga de administrar los usuarios.
 * @author Masoller, Artegoytia, Galleto, Olivera.
 */
public class Usuario {
    private int idUsuario;
    private String nombre;
    private String usuario;
    private String password;
    private int idRol;
    
    public Retorno altaUsuario(String nombre,
            String usuario,
            String password,
            String password2) {
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
        } else if (!Utiles.validarPass(password)) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("La contraseña debe contener al menos un número y"
                    + " caracter especial");
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
                    String sql = "INSERT INTO usuario " +
                          "(nombre, nombre_usuario, password)"
                            + " VALUES (?, ?, ?)";
                    PreparedStatement ps = c.getConnection().prepareStatement(sql);
                    ps.setString(1, nombre);
                    ps.setString(2, usuario);
                    ps.setString(3, passwordHasheada);
                    ps.executeUpdate();
                    ps.close();
                    
                    // Actualizo el LOG
                    sql = "INSERT INTO log " +
                          "(accion, fecha, hora)"
                            + " VALUES (?, ?, ?)";
                    ps = c.getConnection().prepareStatement(sql);
                    ps.setString(1, "Se dio de alta al usuario: " + usuario);
                    ps.setString(2, Utiles.getFechaActual());
                    ps.setString(3, Utiles.getHoraActual());
                    ps.executeUpdate();
                    ps.close();
                    
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
            
            String sql = "SELECT id_usuario FROM usuario WHERE nombre_usuario = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, usuario);   
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return retorno;
    }
    
    public Retorno eliminarUsuario (String usuario, int idUsuarioLogueado) {
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

                    String sql = "DELETE FROM usuario WHERE nombre_usuario = ?";
                    PreparedStatement ps = c.getConnection().prepareStatement(sql);
                    ps.setString(1, usuario);
                    ps.executeUpdate();
                    ps.close();
                    
                    // Actualizo el LOG
                    sql = "INSERT INTO log " +
                          "(id_usuario, accion, fecha, hora)"
                            + " VALUES (?, ?, ?, ?)";
                    ps = c.getConnection().prepareStatement(sql);
                    ps.setInt(1, idUsuarioLogueado);
                    ps.setString(2, "Se borró el usuario: " + usuario);
                    ps.setString(3, Utiles.getFechaActual());
                    ps.setString(4, Utiles.getHoraActual());
                    ps.executeUpdate();
                    ps.close();
                    
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
    
    public List<String> listarUsuarios (int idUsuarioLogueado) {
        List<String> listaUsuarios = new ArrayList<>();

        try {                       
            Conexion c = new Conexion();
            Statement stmt = c.getConnection().createStatement();

            String sql = "SELECT nombre_usuario FROM usuario"
                    + " WHERE id_usuario <> " + idUsuarioLogueado;
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
    
    public static Rol getRolUsuario (String usuario) {
        Rol rol = new Rol();
        
        try {            
            Conexion c = new Conexion();

            String sql = "SELECT r.id_rol, r.descripcion FROM rol r"
                    + " JOIN usuario u"
                    + " ON r.id_rol = u.id_rol"
                    + " WHERE u.nombre_usuario = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
            }
            
            ps.close();
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return rol;
    }
    
    public static Retorno agregarRol (String usuario, String rol, int idUsuario) {
        Retorno retorno = new Retorno();
        
        try {            
            Conexion c = new Conexion();

            String sql = "SELECT id_rol FROM rol"
                    + " WHERE descripcion = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();
            
            int idRol = 0;
            if (rs.next()) {
                idRol = rs.getInt("id_rol");
            }
            
            sql = "UPDATE usuario SET id_rol = ? WHERE nombre_usuario = ?";
            ps = c.getConnection().prepareStatement(sql);
            ps.setInt(1, idRol);
            ps.setString(2, usuario);
            ps.executeUpdate();
            
            // Actualizo el LOG
            sql = "INSERT INTO log " +
                  "(id_usuario, accion, fecha, hora)"
                    + " VALUES (?, ?, ?, ?)";
            ps = c.getConnection().prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, "Se agregó el rol: " + rol + " a: " + usuario);
            ps.setString(3, Utiles.getFechaActual());
            ps.setString(4, Utiles.getHoraActual());
            ps.executeUpdate();
            
            ps.close();
            rs.close();
            
            retorno.setCodigo(0);
            retorno.setDescripcion("Éxito en la operación");
            
        } catch (SQLException e) {
            retorno.setCodigo(-3);
            retorno.setDescripcion("Error en consulta SQL");
        }  
        
        return retorno;
    }
    
    public static Retorno modificarRol (String usuario, String rol, int idUsuarioLogueado) {
        Retorno retorno = new Retorno();
        
        try {            
            Conexion c = new Conexion();

            String sql = "SELECT id_rol FROM rol"
                    + " WHERE descripcion = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, rol);
            ResultSet rs = ps.executeQuery();
            
            int idRol = 0;
            if (rs.next()) {
                idRol = rs.getInt("id_rol");
            }
            
            sql = "UPDATE usuario SET id_rol = ? WHERE nombre_usuario = ?";
            ps = c.getConnection().prepareStatement(sql);
            ps.setInt(1, idRol);
            ps.setString(2, usuario);
            ps.executeUpdate();
            
            // Actualizo el LOG
            sql = "INSERT INTO log " +
                  "(id_usuario, accion, fecha, hora)"
                    + " VALUES (?, ?, ?, ?)";
            ps = c.getConnection().prepareStatement(sql);
            ps.setInt(1, idUsuarioLogueado);
            ps.setString(2, "Se modificó el rol: " + rol + " a: " + usuario);
            ps.setString(3, Utiles.getFechaActual());
            ps.setString(4, Utiles.getHoraActual());
            ps.executeUpdate();
            
            ps.close();
            rs.close();
            
            retorno.setCodigo(0);
            retorno.setDescripcion("Éxito en la operación");
            
        } catch (SQLException e) {
            retorno.setCodigo(-3);
            retorno.setDescripcion("Error en consulta SQL");
        }  
        
        return retorno;
    }
    
    public static Retorno eliminarRol (String usuario, int idUsuarioLogueado) {
        Retorno retorno = new Retorno();
        
        try {            
            Conexion c = new Conexion();
            
            String sql = "UPDATE usuario SET id_rol = null WHERE nombre_usuario = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, usuario);
            ps.executeUpdate();
            
            // Actualizo el LOG
            sql = "INSERT INTO log " +
                  "(id_usuario, accion, fecha, hora)"
                    + " VALUES (?, ?, ?, ?)";
            ps = c.getConnection().prepareStatement(sql);
            ps.setInt(1, idUsuarioLogueado);
            ps.setString(2, "Se eliminó el rol a: " + usuario);
            ps.setString(3, Utiles.getFechaActual());
            ps.setString(4, Utiles.getHoraActual());
            ps.executeUpdate();
            
            ps.close();
            
            retorno.setCodigo(0);
            retorno.setDescripcion("Éxito en la operación");
            
        } catch (SQLException e) {
            retorno.setCodigo(-3);
            retorno.setDescripcion("Error en consulta SQL");
        }  
        
        return retorno;
    }
    
    public static List<Permiso> listarPermisosUsuario (String usuario) {
        List<Permiso> listaPermisos = new ArrayList<>();

        try {            
            Conexion c = new Conexion();

            String sql = "SELECT p.id_permiso, p.descripcion FROM permiso p"
                    + " JOIN rol_permiso rp"
                    + " ON p.id_permiso = rp.id_permiso"
                    + " JOIN usuario u"
                    + " ON rp.id_rol = u.id_rol"
                    + " WHERE u.nombre_usuario = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                do {
                    Permiso p = new Permiso();
                    p.setIdPermiso(rs.getInt("id_permiso"));
                    p.setDescripcion(rs.getString("descripcion"));
                    listaPermisos.add(p);
                } while (rs.next());
            }
            
            ps.close();
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return listaPermisos;
    } 
    
/*    public static Retorno agregarPermiso(String usuario, String permiso, int idUsuario) {
        Retorno retorno = new Retorno();
        
        try {            
            Conexion c = new Conexion();

            String sql = "UPDATE "
                    + " JOIN rol_permiso rp"
                    + " ON p.id_permiso = rp.id_permiso"
                    + " JOIN usuario u"
                    + " ON rp.id_rol = u.id_rol"
                    + " WHERE u.nombre_usuario = ?";
            PreparedStatement ps = c.getConnection().prepareStatement(sql);
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                do {
                    Permiso p = new Permiso();
                    p.setIdPermiso(rs.getInt("id_permiso"));
                    p.setDescripcion(rs.getString("descripcion"));
                    listaPermisos.add(p);
                } while (rs.next());
            }
            
            ps.close();
            rs.close();
            
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }        
        
        return retorno;
    }*/
    
    public RetornoUsuario logIn (String usuario, String password) {
        RetornoUsuario retorno = new RetornoUsuario();
        
        if (password.length() != 16) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El largo de la constraseña debe ser de 16 caracteres");
        } else if (usuario.length() == 0) {
            retorno.setCodigo(-1);
            retorno.setDescripcion("El usuario no puede ser vacío");
        } else {
            try {
                String passwordHasheada = hashToPassword(password);
                Conexion c = new Conexion();
                
                // Verifico que no exista el usuario en la base antes de insertarlo
                boolean yaExiste = consultaUsuario(usuario);
                
                if (!yaExiste) {
                    retorno.setCodigo(-4);
                    retorno.setDescripcion("El usuario " + usuario + " no existe");
                } else {
                    String sql = "SELECT id_usuario, nombre_usuario, id_rol FROM usuario"
                            + " WHERE nombre_usuario = ? AND password = ?";
                    PreparedStatement ps = c.getConnection().prepareStatement(sql);
                    ps.setString(1, usuario);
                    ps.setString(2, passwordHasheada);
                    ResultSet rs = ps.executeQuery();
            
                    Usuario u = new Usuario();
                    if (rs.next()) {                        
                        u.setIdUsuario(rs.getInt("id_usuario"));
                        u.setUsuario(rs.getString("nombre_usuario"));
                        u.setIdRol(rs.getInt("id_rol"));
                    } else {
                        retorno.setCodigo(-1);
                        retorno.setDescripcion("Usuario y/o contraseña incorrectos");
                    }
                    retorno.setUsuario(u);
                    
                    ps.close();
                    rs.close();
                    
                    // Actualizo el LOG
                    sql = "INSERT INTO log " +
                          "(id_usuario, accion, fecha, hora)"
                            + " VALUES (?, ?, ?, ?)";
                    ps = c.getConnection().prepareStatement(sql);
                    ps.setInt(1, u.getIdUsuario());
                    ps.setString(2, "Se logueo el usuario: " + u.getUsuario());
                    ps.setString(3, Utiles.getFechaActual());
                    ps.setString(4, Utiles.getHoraActual());
                    ps.executeUpdate();
                    ps.close();
                    
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

    /**
     * @return the idUsuario
     */
    public int getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idUsuario the idUsuario to set
     */
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

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
}
