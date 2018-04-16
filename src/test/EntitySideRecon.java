package test;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.persistence.Table;


/**
 *
 * @author AezA
 */
public class EntitySideRecon {

    
    static Hashtable<String, ArrayList<String>> enHt = new Hashtable<>();
    static Hashtable<String, String> tableClassHt = new Hashtable<>();

    public void searchAndPut(String className) {

        String key;
        ArrayList<String> list = new ArrayList<>();
        Class cl = null;
        try {

            cl = Class.forName(className);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            key = ((Table) cl.getAnnotation(Table.class)).name();
            tableClassHt.put(key, cl.getSimpleName());
        } catch (NullPointerException e) {
            System.out.println("******  this class does not have a Table: " + className);
            return;
        }

        Field[] fields = cl.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            for (Annotation an : field.getDeclaredAnnotations()) {

                String sw = an.toString().replace("@javax.persistence.", "")
                        .replaceFirst("\\(.*", "");

                switch (sw) {

                    case "ManyToOne":
                        list.add("ManyToOne: " + field.getType()
                                .getAnnotation(Table.class)
                                .name());
                        break;

                    case "OneToMany":
                        list.add("OneToMany: " + ((Class<?>) (((ParameterizedType) field
                                .getGenericType())
                                .getActualTypeArguments()[0]))
                                .getAnnotation(Table.class)
                                .name());
                        break;

                    case "OneToOne":
                        list.add("OneToOne: " + field.getType()
                                .getAnnotation(Table.class)
                                .name());
                        break;
                }//switch ends
            }//for annotation ends
        }//for field ends
        enHt.put(key, list);
    }//method put ends
}
