/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.upm.fi.dia.oeg.map4rdf.server.db;

import com.google.inject.Singleton;
import es.upm.fi.dia.oeg.map4rdf.share.ConfigPropertie;
import es.upm.fi.dia.oeg.map4rdf.share.conf.ParameterNames;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mindrot.jbcrypt.BCrypt;
/**
 *
 * @author filip
 */
@Singleton
public final class SQLconnector {
    private Connection conn = null;
    private String status;

    public SQLconnector() throws ClassNotFoundException {
        
        try {
            //check that db exists
            File dbFile = new File(DbConfig.DB_NAME);
            Boolean init = true;
            if (dbFile.exists()) {
                init = false;
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + DbConfig.DB_NAME);
            conn.setAutoCommit(true);
   
            if (init) {
                //init seed values for database
                    Statement statement = conn.createStatement();
                    statement.executeUpdate("CREATE TABLE config (key TEXT NOT NULL PRIMARY KEY, value TEXT NOT NULL)");
                    statement.close();
                //initiate all properties
                for (String key : DbConfig.DB_SEED.keySet()) {
                    addPropertie(key, DbConfig.DB_SEED.get(key));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Boolean addPropertie(String key, String value)  {
        try {
            //check that connection and statement are ok
             PreparedStatement prep = conn.prepareStatement("insert into config values (?, ?);");
             prep.setString(1, key);
             if(key.equals(ParameterNames.ADMIN)) {
                prep.setString(2,crypt(value));
             } else {
                prep.setString(2,value);
             }
             prep.addBatch();
             prep.executeBatch();
             prep.close();
             return true;
        } catch (SQLException ex) {
           return false;
        }
    }
 

    public List<ConfigPropertie> getProperties(List<String> keys) {
        ArrayList<ConfigPropertie> resultList = new ArrayList<ConfigPropertie>();
        try {
            for (String key : keys) {
                PreparedStatement prep = conn.prepareStatement("SELECT key, value FROM config WHERE key=?;");
                prep.setString(1,key);
                ResultSet rs = prep.executeQuery();
                while(rs.next()) {
                    resultList.add(new ConfigPropertie(rs.getString("key"),rs.getString("value")));
                }
                prep.close();
                rs.close();
            }

        } catch (SQLException ex) {

        }
        return resultList;
    }

    public Boolean setProperties(List<ConfigPropertie> properties) {
     
      for(ConfigPropertie prop : properties) {
            try {
                PreparedStatement prep = conn.prepareStatement("UPDATE config SET value=? WHERE key=?");
                if(prop.getKey().equals("admin")) {
                    prep.setString(1,crypt(prop.getValue()));
                } else {
                prep.setString(1,prop.getValue());
                }
                prep.setString(2,prop.getKey());
                prep.executeUpdate();
                prep.close();
            } catch (SQLException ex) {
                return false;
            }
      }
      return true;
    }

    public Boolean login(String password){ 
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT value FROM config WHERE key='admin'");
            String a = rs.getString("value");
            statement.close();
            rs.close();
            Boolean res =  BCrypt.checkpw(password, a);
            return res;
        } catch (SQLException ex) {
            //Logger.getLogger(SQLconnector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public String getValue(String key) {
        ArrayList<String> list = new  ArrayList<String>();
        list.add(key);
        List<ConfigPropertie> resultList = getProperties(list);
        if(resultList!=null && resultList.size()>0) {
            return resultList.get(0).getValue();
        } else {
            return "";        
        }
    }

    private String crypt(String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt(12));
    }
}
