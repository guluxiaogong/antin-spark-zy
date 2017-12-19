package com.antin.recom.helper;

import org.apache.commons.lang.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2017/9/4.
 */
public class ResultHelper {

    protected static Map<String, String> labels = new ConcurrentHashMap<>();

    public static List queryAsJson(ResultSet rs) throws SQLException {
        List array = new ArrayList();
        List<String> names = new ArrayList<>();
        int column = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= column; i++)
            names.add(rs.getMetaData().getColumnLabel(i));
        for (; rs.next(); ) {
            Map object = new HashMap();
            for (String name : names)
                object.put(formatColumnLabel(name), rs.getObject(name));
            array.add(object);
        }
        // rs.close();
        return array;
    }

    protected static String formatColumnLabel(String label) {
        if (StringUtils.isEmpty(label))
            return label;

        String string = labels.get(label);
        if (string != null)
            return string;

        StringBuffer sb = new StringBuffer();
        boolean line = false;
        for (char ch : label.toLowerCase().toCharArray()) {
            if (ch == '_') {
                line = true;

                continue;
            }

            if (line) {
                line = false;
                sb.append((char) (ch >= 'a' && ch <= 'z' ? (ch - 'a' + 'A') : ch));

                continue;
            }

            sb.append(ch);
        }
        string = sb.toString();
        labels.put(label, string);

        return string;
    }
}
