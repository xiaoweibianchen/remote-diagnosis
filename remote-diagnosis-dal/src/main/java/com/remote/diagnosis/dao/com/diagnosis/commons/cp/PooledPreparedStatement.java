package com.remote.diagnosis.dao.com.diagnosis.commons.cp;

import com.remote.diagnosis.dao.com.diagnosis.commons.cp.util.JdbcUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

class PooledPreparedStatement extends PooledStatement {
    private static final Logger log = new Logger();
    
    private PreparedStatement pstmt;
    private PreparedStatement real_pstmt;
    
    private final String sql;
    private final Object paras[];

    private String toString;

    PooledPreparedStatement(PooledConnection conn, PreparedStatement stmt, int stmtId, String sql) throws SQLException {
        super(conn, stmt, stmtId);
        real_pstmt = (PreparedStatement) getStatement();
        this.sql = sql == null ? "" : JdbcUtil.multiLinesToOneLine(sql, " ");
        this.paras = new Object[getQMCount()];
    }

    /*
    * Do nothing for generic PooledPreparedStatement. For subclass implementation.
    */
    public void cleanCache() {}

    @SuppressWarnings("unchecked")
    protected PreparedStatement buildProxy() {
        Statement stmt = getStatement();
        Class[] intfs = stmt.getClass().getInterfaces();
        boolean impled = false; //是否实现了Connection接口
        for (Class intf: intfs) {
            if (intf.getName().equals(PreparedStatement.class.getName())) {
                impled = true;
                break;
            }
        }
        if (!impled) {
            //没有实现Connection接口，则强制增加
            Class[] tmp = intfs;
            intfs = new Class[tmp.length + 1];
            System.arraycopy(tmp, 0, intfs, 0, tmp.length);
            intfs[tmp.length] = PreparedStatement.class;
        }
        pstmt = (PreparedStatement) Proxy.newProxyInstance(stmt.getClass().getClassLoader(), intfs, this);
        return pstmt;
    }

    protected String getSqlDoing() {
        return toString();
    }

    protected Object _invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        Object ret = null;
        toString = null;
        try {
            if (methodDoing.equals("addBatch") && (args == null || args.length == 0)) {
                real_pstmt.addBatch();
                if (isPrintSQL()) {
                    printSQL(log, (System.nanoTime() - start));
                }
            } else if (methodDoing.equals("execute") && (args == null || args.length == 0)) {
                ret = real_pstmt.execute();
                if (isPrintSQL()) {
                    printSQL(log, (System.nanoTime() - start), "[", ret, "]");
                }
            } else if (methodDoing.equals("executeQuery") && (args == null || args.length == 0)) {
                resultSet = real_pstmt.executeQuery();
                ret = resultSet;
                if (isPrintSQL()) {
                    printSQL(log, (System.nanoTime() - start));
                }
            } else if (methodDoing.equals("executeUpdate") && (args == null || args.length == 0)) {
                ret = real_pstmt.executeUpdate();
                if (isPrintSQL()) {
                    printSQL(log, (System.nanoTime() - start), "[", ret, "]");
                }
            } else if (methodDoing.startsWith("set") && args.length == 2 && args[0] instanceof Integer) {
                int idx = ((Integer) args[0]).intValue();
                if (args[1] == null) {
                    args[1] = "";
                }
                if (paras.length >= idx) {
                    paras[idx - 1] = args[1];
                }
                ret = method.invoke(real_pstmt, args);
            } else if (methodDoing.equals("toString") && (args == null || args.length == 0)) {
                ret = toString();
            } else {
                ret = super._invoke(proxy, method, args);
            }
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } 
        return ret;
    }

    private int getQMCount() {
        int c = 0;
        int idx = 0;
        while (true) {
            idx = sql.indexOf("?", idx + 1);
            if (idx == -1) {
                break;
            }
            c++;
        }
        return c;
    }

    public String toString() {
        if (toString != null) {
            return toString;
        }
        if (paras == null) {
            return sql;
        }
        StringBuilder sb = new StringBuilder(sql.length() + paras.length * 16);
        int idx = 0;
        for (int i = 0; i < paras.length; i++) {
            Object p = paras[i];
            int idxNext = sql.indexOf("?", idx);
            if (p == null) {
                idx = idxNext+1;
                continue;
            }
            if (idxNext < 0) {
                break;
            }
            sb.append(sql.substring(idx, idxNext));
            if (p instanceof String || p instanceof java.sql.Time || p instanceof java.sql.Timestamp || p instanceof java.sql.Date) {
                sb.append('\'').append(p).append('\'');
            } else {
                sb.append(p);
            }
            idx = idxNext+1;
        }
        sb.append(sql.substring(idx));
        toString = sb.toString();
        return toString;
    }
}
