package vars.query.util;

/**
 * Created by brian on 12/30/13.
 */
public interface Dataset {

    String[] getColumnNames();
    Object[] getData(String columnName);
    int getRowCount();
    int getColumnCount();

}
