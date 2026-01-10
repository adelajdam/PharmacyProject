package dao;

import Model.SystemConfig;

public interface SystemConfigDao {

    void save(SystemConfig config);
    void update(String key, String value);
    SystemConfig findByKey(String key);
}

