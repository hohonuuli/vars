package vars.query.util;

import com.google.common.collect.Maps;

import java.util.*;

/**
 * Created by brian on 12/30/13.
 */
public class MutableSimpleDataset implements Dataset {

    private final Map<String, List<Object>> data = Maps.newHashMap();

    public MutableSimpleDataset(String[] columnNames) {
        for (String cn: columnNames) {
            data.put(cn, new ArrayList<Object>());
        }
    }

    @Override
    public String[] getColumnNames() {
        String[] cns = (String[]) data.keySet().toArray();
        Arrays.sort(cns);
        return cns;
    }

    @Override
    public Object[] getData(String columnName) {
        List<Object> column = data.get(columnName);
        Object[] columnData = null;
        if (column != null) {
            columnData = column.toArray();
        }
        return columnData;
    }

    @Override
    public int getRowCount() {
        int rows = 0;
        for (List<Object> v : data.values()) {
            rows = v.size();
            break;
        }
        return rows;
    }

    @Override
    public int getColumnCount() {
        return data.keySet().size();
    }

    public void addRow(Map<String, Object> rowData) {
        for (String key: data.keySet()) {
            List<Object> xs = data.get(key);
            if (rowData.containsKey(key)) {
                xs.add(rowData.get(key));
            }
            else {
                xs.add(null);
            }
        }
    }
}
