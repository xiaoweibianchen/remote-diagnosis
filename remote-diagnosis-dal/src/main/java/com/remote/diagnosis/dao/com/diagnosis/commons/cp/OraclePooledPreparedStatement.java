package com.remote.diagnosis.dao.com.diagnosis.commons.cp;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.remote.diagnosis.dao.com.diagnosis.commons.cp.util.OracleUtil;

/**
 *
 */
class OraclePooledPreparedStatement extends PooledPreparedStatement {
	 private static final Logger logger= LoggerFactory.getLogger(OraclePooledPreparedStatement.class);

    private boolean useOracleImplicitCache;

    OraclePooledPreparedStatement(PooledConnection conn, PreparedStatement stmt, int stmtId, String sql,
                                  boolean useOracleImplicitCache) throws SQLException {
        super(conn, stmt, stmtId, sql);
        this.useOracleImplicitCache = useOracleImplicitCache;
    }

    @Override
    public void cleanCache() {
        if (useOracleImplicitCache) {
            OracleUtil.enterImplicitCache(this.getStatement());
        }
    }

    @Override
    public Statement checkOut() throws SQLException {
        if (useOracleImplicitCache) {
            OracleUtil.exitImplicitCacheToActive(this.getStatement());
        }
        return super.checkOut();
    }

    @Override
    public void close() {
        if (useOracleImplicitCache) {
            OracleUtil.exitImplicitCacheToClose(this.getStatement());
        }
        super.close();
    }

}
