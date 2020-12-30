package solutions.vpf;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Random rand = new Random();
        try(
            Connection conn = connect("jdbc:sqlite:./tests.sqlite");
            ){
                if(conn == null){
                    System.err.println("Error de conexión");
                    return;
                }

                conn.setAutoCommit(false);
                execute(conn, "CREATE TABLE IF NOT EXISTS test(id integer PRIMARY KEY AUTOINCREMENT, value text);");
                try (PreparedStatement stmt = conn.prepareStatement("select id, value from test")) {
                    ResultSet rset = selectSql(stmt, null);
                
                    if(rset == null){
                        System.err.println("Error al obtener datos");
                        return;
                    }
    
                    List<Object> params = Arrays.asList("", 0);
                    String updatesql = "update test set value=? where id=?";
                    while (rset.next()){
                        params.set(0, rset.getString("value") + "-" + rand.nextInt());
                        params.set(1, rset.getInt("id"));
                        updateSql(conn, updatesql, params);
                    }
                } catch (Exception e) {
                    //TODO: handle exception
                }
                conn.commit();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
    }
   /**
     * Connect to the test.db database
     *
     * @return the Connection object
     */
    private static Connection connect(String url) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.err.println("Connect:" + e.getMessage());
        }
        return conn;
    }

    public static void execute(Connection conn, String sql) {
        try (Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Execute" + e.getMessage());
        }
    }

    /**
     * Insert a new row into the warehouses table
     *
     * @param connection
     * @param sql
     * @param paramlist
     * @return int lastid
     * 
     */
    public static int updateSql(Connection connection, String sql, List<Object> paramlist) {
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < paramlist.size(); i++) {
                pstmt.setObject(i+1, paramlist.get(i));
            }
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }else{
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("update" + e.getMessage());
            return -1;
        }
    }

        /**
     * Insert a new row into the warehouses table
     *
     * @param connection
     * @param sql
     * @param paramlist
     * @return int lastid
     * 
     */
    public static ResultSet selectSql(PreparedStatement pstmt, List<Object> paramlist) {
        try {
            if(paramlist != null){
                for (int i = 0; i < paramlist.size(); i++) {
                    pstmt.setObject(i+1, paramlist.get(i));
                }
            }
            return pstmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("update" + e.getMessage());
            return null;
        }
    }
}
