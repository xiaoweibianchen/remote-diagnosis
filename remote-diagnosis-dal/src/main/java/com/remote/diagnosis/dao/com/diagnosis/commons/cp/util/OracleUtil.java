package com.remote.diagnosis.dao.com.diagnosis.commons.cp.util;

import oracle.jdbc.internal.OraclePreparedStatement;

import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OracleUtil {
	 private static final Logger log= LoggerFactory.getLogger(OracleUtil.class);

    public final static String ORACLE_FREECACHE_PROPERTY_NAME = "oracle.jdbc.FreeMemoryOnEnterImplicitCache";
    public final static String ORACLE_FREECACHE_PROPERTY_VALUE_TRUE = "true";

    public final static String SOCKET_TIMEOUT = "oracle.jdbc.ReadTimeout";
    public final static String SOCKET_TIMEOUT_LOW_VER = "oracle.net.READ_TIMEOUT";

    public final static String CONNECT_TIMEOUT = "oracle.net.CONNECT_TIMEOUT";

    /**
     * Will clear the PS caches for this statement in driver.
     * Not working on version 10 driver. Working on v11.
     */
    public static void enterImplicitCache(Statement statement) {
        try {
            OraclePreparedStatement oraclePreparedStatement = unwrapInternal(statement);

            if (oraclePreparedStatement != null) {
                oraclePreparedStatement.enterImplicitCache();
            }
        } catch(SQLException e) {
        	log.warn("数据库异常:{}",e);
        }
    }

    /**
     * Call when the PreparedStatement re-used.
     */
    public static void exitImplicitCacheToActive(Statement statement) {
        try {
            OraclePreparedStatement oraclePreparedStatement = unwrapInternal(statement);

            if (oraclePreparedStatement != null) {
                oraclePreparedStatement.exitImplicitCacheToActive();
            }
        } catch (SQLException e) {
            log.warn("数据库异常:{}",e);
        }
    }

    /**
     * Call when the prepared statement needs to be removed.
     */
    public static void exitImplicitCacheToClose(Statement statement) {
        try {
        OraclePreparedStatement oraclePreparedStatement = unwrapInternal(statement);

        if (oraclePreparedStatement != null) {
            oraclePreparedStatement.exitImplicitCacheToClose();
        }
        } catch(SQLException e) {
        	log.warn("数据库异常:{}",e);
        }
    }

    /**
     * Unwrap Statement to get the internal OraclePreparedStatement.
     */
    private static OraclePreparedStatement unwrapInternal(Statement stmt) throws SQLException {
        if (stmt instanceof OraclePreparedStatement) {
            return (OraclePreparedStatement) stmt;
        }

        OraclePreparedStatement unwrapped = stmt.unwrap(OraclePreparedStatement.class);

        if (unwrapped == null) {
            log.error("can not unwrap statement : " + stmt.getClass());
        }

        return unwrapped;
    }

}
