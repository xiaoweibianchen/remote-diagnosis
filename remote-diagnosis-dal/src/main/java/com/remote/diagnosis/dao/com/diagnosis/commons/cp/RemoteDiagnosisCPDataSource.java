package com.remote.diagnosis.dao.com.diagnosis.commons.cp;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RemoteDiagnosisCPDataSource extends RemoteDiagnosisCPConfig implements DataSource, ObjectFactory {

    private ReadWriteLock rwl = new ReentrantReadWriteLock();

    private RemoteDiagnosisCP pool = null;
    private PrintWriter logWriter = null;

    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
    }

//    public void setLoginTimeout(int seconds) throws SQLException {
//        throw new UnsupportedOperationException("setLoginTimeout is unsupported.");
//    }
//
//    public int getLoginTimeout() throws SQLException {
//        throw new UnsupportedOperationException("getLoginTimeout is unsupported.");
//    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    public Object getObjectInstance(Object object, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {

        Reference ref = (Reference) object;
        Enumeration<RefAddr> addrs = ref.getAll();
        Properties props = new Properties();
        while (addrs.hasMoreElements()) {
            RefAddr addr = addrs.nextElement();
            if (addr.getType().equals("driverClassName") || addr.getType().equals("driver")) {
                //TODO test the logical is correct?
                Class.forName((String) addr.getContent());
            } else {
                props.put(addr.getType(), addr.getContent());
            }
        }
        RemoteDiagnosisCPDataSource ds = new RemoteDiagnosisCPDataSource();
        ds.setProperties(props);
        return ds;
    }

    public Connection getConnection() throws SQLException {
        if (this.pool == null) {
            maybeInit();
        }
        return this.pool.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("getConnection(username, password) is unsupported.");
    }

    private void maybeInit() throws SQLException {

        try {
            this.rwl.readLock().lock();
            if (this.pool == null) { // this.pool is protected in getConnection
                this.rwl.readLock().unlock();
                this.rwl.writeLock().lock();
                try {
                    if (this.pool == null) { // read might have passed, write
                        // might not
                        this.pool = new RemoteDiagnosisCP(this);
                    }
                } finally {
                    this.rwl.readLock().lock();
                    this.rwl.writeLock().unlock();
                }
            }
        } finally {
            this.rwl.readLock().unlock();
        }
    }


}
