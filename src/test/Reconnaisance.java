package test;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author AezA
 */
public class Reconnaisance {
    private static final String FOLDER_PATH = "";

    private static File file = new File(FOLDER_PATH);
    private ArrayList<String> classNamesList = new ArrayList<>();
    private ArrayList<String> tableNamesList = new ArrayList<>();
    private final String IP_ADDRESS = "";
    private final String DATABASE_NAME = "";
    private final String PASSWORD = "";

    private Connection connect() {

        String url = "jdbc:sqlserver://"+IP_ADDRESS+";databaseName=" + DATABASE_NAME;
        String user = "sa";

        Connection cn = null;
        try {
            cn = DriverManager.getConnection(url, user, PASSWORD);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cn;
    }

    void getClassNames(File file) {

        if (file.isDirectory()) {

            for (File f : file.listFiles()) {

                getClassNames(f);
            }
        }

        if (file.isFile()) {

            classNamesList.add(file.getAbsolutePath().replace(FOLDER_PATH.substring(0, 3), "").replace("\\", ".").replace(".java", ""));
        }
    }

    void getTableNames(Connection con) {

        DatabaseMetaData md = null;

        try {
            md = con.getMetaData();
            String[] st = {"TABLE"};
            ResultSet rs = md.getTables(null, "dbo", "%", st);

            while (rs.next()) {

                tableNamesList.add(rs.getString("TABLE_NAME"));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    void reckoningEntitySide(Connection conn) {
    
        DBSideRecon dBSideRecon = new DBSideRecon();
        EntitySideRecon entitySideRecon = new EntitySideRecon();
        
        for(String db: tableNamesList) {
            dBSideRecon.searchAndPut(db, conn);
        }
        
        for(String en: classNamesList) {
            entitySideRecon.searchAndPut(en);
        }
        
/*-----------------------------------------------------------------------------*/
        for(String eKey : EntitySideRecon.enHt.keySet()) {
            boolean sw1 = false;
            for(String dKey : DBSideRecon.dbHt.keySet()) {
                if(eKey.equals(dKey)) {
                    sw1 = true;
                    for(String eTable : EntitySideRecon.enHt.get(eKey)) { 
                        boolean sw2 = false;
                        for(String dTable : DBSideRecon.dbHt.get(dKey)) {
                            if(eTable.equals(dTable)) {
                                sw2 = true;
                            }
                        }
                        if(!sw2) {
                            System.out.println(eKey + " doesn't have " + eTable + " in Database.\n");
                        }    
                    }
                }     
            }
            if(!sw1) {
                System.out.println("There's no Table as " + eKey + " inside Database.\n");
            }  
        }
    }
    
    void reckoningDatabaseSide(Connection conn) {

        DBSideRecon dBSideRecon = new DBSideRecon();
        EntitySideRecon entitySideRecon = new EntitySideRecon();

        String className;
        String tableName;
        
        for (String db : tableNamesList) {
            dBSideRecon.searchAndPut(db, conn);
        }

        for (String en : classNamesList) {
            entitySideRecon.searchAndPut(en);
        }
        
        /*-----------------------------------------------------------------------------*/
        for (String dKey : DBSideRecon.dbHt.keySet()) {
            boolean sw1 = false;
            for (String eKey : EntitySideRecon.enHt.keySet()) {
                if (dKey.equals(eKey)) {
                    sw1 = true;
                    for (String dTable : DBSideRecon.dbHt.get(dKey)) {
                        boolean sw2 = false;
                        for (String eTable : EntitySideRecon.enHt.get(eKey)) {
                            if (dTable.equals(eTable)) {
                                sw2 = true;
                            }
                        }
                        if (!sw2) {
                            
                            className = EntitySideRecon.tableClassHt.get(dKey);
                            String s = EntitySideRecon.tableClassHt.get(dTable.replaceAll("ManyToOne: |OneToMany: |OneToOne: ", ""));
                            tableName =  s!=null ? dTable.replaceAll(":.*", ": ") + s : dTable ;

                            System.out.println(className + " doesn't have " + tableName + " in Entity.\n");
                        }
                    } 
                }     
            }
            if (!sw1) {
                
                System.out.println("There's no Entity for " + dKey + " inside Project.\n");
            }
        }
        
    }
    
    public static void main(String[] args) {

        Reconnaisance r = new Reconnaisance();
        Connection cn = r.connect();
        
        r.getTableNames(cn);
        r.getClassNames(file);
        r.reckoningEntitySide(cn);
        r.reckoningDatabaseSide(cn);
        
    }
}
