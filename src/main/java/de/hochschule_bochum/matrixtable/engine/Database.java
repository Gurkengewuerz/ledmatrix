package de.hochschule_bochum.matrixtable.engine;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 18.07.2017.
 */
public class Database {
    public static Database db = new Database("database.sqlite3");

    private String sUrl = null;
    private int iTimeout = 30;
    private Connection conn = null;
    private Statement statement = null;


    public Database(String sUrlToLoad) {
        setUrl(sUrlToLoad);
        try {
            setConnection();
            setStatement();
            executeUpdate("CREATE TABLE IF NOT EXISTS devices (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    "mac CHAR(12) NOT NULL UNIQUE," +
                    "username VARCHAR NOT NULL" +
                    ")");
            executeUpdate("CREATE TABLE IF NOT EXISTS score (" +
                    "score_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ," +
                    "score FLOAT NOT NULL ," +
                    "user_id INTEGER NOT NULL," +
                    "created INTEGER," +
                    "game VARCHAR," +
                    "FOREIGN KEY(user_id) REFERENCES devices(user_id)" +
                    ")");
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void setUrl(String sUrlVar) {
        sUrl = sUrlVar;
    }

    private void setConnection() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite:" + sUrl);
    }


    public Connection getConnection() {
        return conn;
    }

    private void setStatement() throws Exception {
        if (conn == null) {
            setConnection();
        }
        statement = conn.createStatement();
        statement.setQueryTimeout(iTimeout);  // set timeout to 30 sec.
    }

    public PreparedStatement getPreparedStatement(String sql) {
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public boolean executeUpdate(String instruction, Object... obj) {
        PreparedStatement pst = getPreparedStatement(instruction);
        try {
            for (int i = 0; i < obj.length; i++) {
                pst.setObject(i + 1, obj[i]);
            }
            pst.execute();
            return true;
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public ResultSet executeQuery(String instruction, Object... obj) {
        PreparedStatement pst = getPreparedStatement(instruction);
        try {
            for (int i = 0; i < obj.length; i++) {
                pst.setObject(i + 1, obj[i]);
            }
            return pst.executeQuery();
        } catch (SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (Exception ignore) {
        }
    }
}
