package obligatorioseguridad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private Connection connection;
    
    public Conexion() throws SQLException {
        try {
            connection = DriverManager
                            .getConnection("jdbc:postgresql://127.0.0.1:5432/SeguridadDB", 
                                    "postgres",
				    "admin");
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }
    }
    
    public void close() { 
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("SQLException " + e);
        }
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
