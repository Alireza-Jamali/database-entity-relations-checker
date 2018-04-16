package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author AezA
 */
public class DBSideRecon {

    static HashMap<String, ArrayList<String>> dbHt = new HashMap<>();

    void searchAndPut(String tableName, Connection cn) {

        ArrayList<String> list = new ArrayList<>();

        DatabaseMetaData metaData = null;
        try {
            metaData = cn.getMetaData();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try (ResultSet importedKeys = metaData.getImportedKeys(cn.getCatalog(), "dbo", tableName)) {
            while (importedKeys.next()) {

                if ("Id".equals(importedKeys.getString("FKCOLUMN_NAME"))
                        && "Id".equals(importedKeys.getString("PKCOLUMN_NAME"))) {
                    list.add("OneToOne: " + importedKeys.getString("PKTABLE_NAME"));
                } else {

                    list.add("ManyToOne: " + importedKeys.getString("PKTABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (ResultSet exportedKeys = metaData.getExportedKeys(cn.getCatalog(), "dbo", tableName)) {
            while (exportedKeys.next()) {

                if ("Id".equals(exportedKeys.getString("FKCOLUMN_NAME"))
                        && "Id".equals(exportedKeys.getString("PKCOLUMN_NAME"))) {
                    list.add("OneToOne: " + exportedKeys.getString("FKTABLE_NAME"));
                } else {
                    list.add("OneToMany: " + exportedKeys.getString("FKTABLE_NAME"));
                }
            }

            dbHt.put(tableName, list);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
