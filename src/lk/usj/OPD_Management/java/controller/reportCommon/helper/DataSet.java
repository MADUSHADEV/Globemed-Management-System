package lk.usj.OPD_Management.java.controller.reportCommon.helper;

import java.util.HashMap;
import java.util.Map;

// A simple class to hold the dataset for the report.
// In a real application, this would contain more complex objects like Patient, Billing records, etc.
public class DataSet {
    private final Map<String, Object> data = new HashMap<>();

    public void put(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }
}
