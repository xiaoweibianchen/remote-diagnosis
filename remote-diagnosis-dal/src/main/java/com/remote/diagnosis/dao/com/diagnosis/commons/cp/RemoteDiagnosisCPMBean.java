package com.remote.diagnosis.dao.com.diagnosis.commons.cp;

import java.sql.SQLException;

public interface RemoteDiagnosisCPMBean {

    public String getPoolName();
    public void setPoolName(String poolName);
    
    public int getActiveConnectionsCount();
    public int getIdleConnectionsCount();
    
    public void reloadProperties() throws SQLException;
    public void shutdown();
}
