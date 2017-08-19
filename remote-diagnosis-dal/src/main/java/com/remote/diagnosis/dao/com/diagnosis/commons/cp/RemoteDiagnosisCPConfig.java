package com.remote.diagnosis.dao.com.diagnosis.commons.cp;

import com.remote.diagnosis.dao.com.diagnosis.commons.cp.util.JdbcUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.Logger;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.StringUtil;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.sql.DriverManager;
import java.util.Properties;

public class RemoteDiagnosisCPConfig implements RemoteDiagnosisCPConfigMBean {
    private static final Logger LOGGER = new Logger();
    /**
     * 连接URL
     */
    private String connUrl;
    /**
     * jdbc驱动�?
     */
    private String driver;

    /**
     * 数据库用户名
     */
    private String username;
    /**
     * 数据库用户口�?
     */
    private String password;

    /**
     * 池中�?小连接数
     */
    private int minConnNum = 0;
    /**
     * 池中�?大连接数
     */
    private int maxConnNum = 10;
    /**
     * 池中�?大语句数
     */
    private int maxStmtNum = 100;
    /**
     * 池中�?大语句数
     */
    private int maxPreStmtNum = 10;
    /**
     * 连接�?大空闲时�?(milli sec)(空闲超过该时间的连接将被�?测或回收)
     */
    private long maxIdleMilliSec = 300 * 1000;
    /**
     * 等待空闲连接时的超时时间(ms)
     */
    private long checkOutTimeout = 10000;

    /**
     * 关闭连接时自动提交事�?
     */
    private boolean commitOnClose = false;

    /**
     * 记录除SQL语句及执行时间外的其他信�?
     */
    private boolean verbose = false;

    /**
     * 记录SQL语句及执行时�? //add by wuhq 2011.09.02
     */
    private boolean printSQL = true;

    /**
     * �?测连接是否可用的查询语句
     */
    private String checkStatement;

    /**
     * 0 - no jmx
     * 1 - manage ConnectionFactory instance
     * 2 - manage PooledConnection instance
     */
    private int jmxLevel = 0;

    /**
     * 默认获取的连接的事务模式（true-事务模式，即autocommit=false�?
     */
    private boolean transactionMode = false;

    /**
     * lazy init pool
     * true: init min connections in Monitor thread, else do it in new WangyinCP/getConnection() thread
     */
    private boolean lazyInit = false;

    /**
     * printSQL == true时，打印INFO级别的SQL的�?�时阈�??(ms)
     */
    private long infoSQLThreshold = 10;

    /**
     * printSQL == true时，打印WARN级别的SQL的�?�时阈�??(ms)
     */
    private long warnSQLThreshold = 100;

    /**
     * Indicates if this is for Oracle.
     */
    private boolean isOracle = false;

    /**
     * Indicates if this is MySQL cp
     */
    private boolean isMySQL = false;

    /**
     * Indicates if this is DB2 cp
     */
    private boolean isDB2 = false;

    /**
     * Indicates if oracle implicit preparedstatement cache needed.
     */
    private boolean useOracleImplicitPSCache = true;

    /**
     * connection properties on DriverManager.getConnection(url,info)<br/>
     * �? & 分割属�??
     */
    private Properties connectionProperties = new Properties();

    /**
     * query timeout (seconds)
     */
    private int queryTimeout = 60;

    /*
     * default login timeout 10 seconds
     */
    static {
        DriverManager.setLoginTimeout(10);
    }

    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public void setLoginTimeout(int loginTimeout) {
        DriverManager.setLoginTimeout(loginTimeout);
    }

    public int getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public Properties getConnectionProperties() {
        return connectionProperties;
    }

    public void setConnectionInfo(String connectionInfo) {
        if (connectionInfo == null || connectionInfo.trim().length() == 0) {
            return;
        }

        String[] entries = connectionInfo.split("&");
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            if (entry.length() > 0) {
                int index = entry.indexOf('=');
                if (index > 0) {
                    String name = entry.substring(0, index);
                    String value = entry.substring(index + 1);
                    connectionProperties.setProperty(name, value);
                } else {
                    // no value is empty string which is how java.util.Properties works
                    connectionProperties.setProperty(entry, "");
                }
            }
        }
    }

    public String getConnUrl() {
        return connUrl;
    }

    public void setConnUrl(String connUrl) {
        this.connUrl = connUrl;
        if (this.connUrl != null && this.connUrl.trim().length() != 0) {
            String[] buf = this.connUrl.split(":");
            if (buf.length < 2) {
                return;
            }
            String dbf = buf[1];
            if (dbf.compareToIgnoreCase("oracle") == 0) {
                isOracle = true;
            } else if (dbf.compareToIgnoreCase("mysql") == 0) {
                isMySQL = true;
            } else if (dbf.compareToIgnoreCase("db2") == 0) {
                isDB2 = true;
            }
            if (this.checkStatement == null || this.checkStatement.trim().length() == 0) {
                //if checkStatement NOT be set, auto-set by url
                if (isDB2) {
                    checkStatement = "values(current timestamp)";
                } else if (isOracle) {
                    checkStatement = "select systimestamp from dual";
                } else if (isMySQL) {
                    checkStatement = "select now()";
                }
            }
            if (this.driver == null || this.driver.trim().length() == 0) {
                //if driver NOT be set, auto-set by url
                if (isOracle) {
                    driver = "oracle.jdbc.driver.OracleDriver";
                } else if (isMySQL) {
                    driver = "com.mysql.jdbc.Driver";
                }
            }
        }
    }

    /*
     * 2012-11-12 zhangyao 支持url参数的注入，保持�?其它数据库连接池�?�?
     */
    public String getUrl() {
        return getConnUrl();
    }

    public void setUrl(String url) {
        setConnUrl(url);
    }

    public long getWarnSQLThreshold() {
        return warnSQLThreshold;
    }

    public void setWarnSQLThreshold(long warnSQLThreshold) {
        this.warnSQLThreshold = warnSQLThreshold;
    }

    public long getInfoSQLThreshold() {
        return infoSQLThreshold;
    }

    public void setInfoSQLThreshold(long infoSQLThreshold) {
        this.infoSQLThreshold = infoSQLThreshold;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        if (username != null && username.trim().length() > 0) {
            this.connectionProperties.setProperty("user", username);
        }
    }

    String getPassword() {
        return password;
    }
    private static final String PATTERN_COMMONS_CHARS =  "[\u0020-\u007E]+";
    public void setPassword(String password) {
        if (StringUtil.isEmpty(password)) {
            return;
        }
        this.password = password;//默认直接是密�?
        final boolean isBase64 = Base64.isBase64(password);
        if (isBase64) { // 如果是Base64的尝试解�?
            try {
                byte[] base64DecodedPwd = Base64.decodeBase64(password);
                boolean isBlock16 = (base64DecodedPwd.length % 16 == 0);  //对称加密数据�?定是16的�?�数(AES128)
                if (isBlock16 && !new String(base64DecodedPwd, "UTF-8").matches(PATTERN_COMMONS_CHARS)) { //不可见字符，是加密密�?
                    String plainPwd = StringUtil.trimToEmpty(JdbcUtil.decodePassword(password));//明文密码
                    if (plainPwd.matches(PATTERN_COMMONS_CHARS)) { // 解密结果是可见字�?
                        this.password = plainPwd;
                    }
                }
            } catch (Exception e) {
                LOGGER.debug(e);
                LOGGER.info("使用明文密码: ", e.getMessage());
            }
        }
        this.connectionProperties.setProperty("password", this.password);
    }

    public int getMinConnections() {
        return minConnNum;
    }

    public void setMinConnections(int minConnections) {
        this.minConnNum = minConnections;
    }

    public int getMaxConnections() {
        return maxConnNum;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnNum = maxConnections;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isPrintSQL() {
        return printSQL;
    }

    public void setPrintSQL(boolean printSQL) {
        this.printSQL = printSQL;
    }

    public boolean isCommitOnClose() {
        return commitOnClose;
    }

    public void setCommitOnClose(boolean commitOnClose) {
        this.commitOnClose = commitOnClose;
    }

    public long getIdleTimeoutSec() {
        return maxIdleMilliSec / 1000;
    }

    public long getIdleTimeoutMilliSec() {
        return maxIdleMilliSec;
    }

    public void setIdleTimeoutSec(long idleTimeoutSec) {
        this.maxIdleMilliSec = idleTimeoutSec * 1000;
    }

    public long getCheckoutTimeoutMilliSec() {
        return checkOutTimeout;
    }

    public void setCheckoutTimeoutMilliSec(long checkoutTimeoutMilliSec) {
        this.checkOutTimeout = checkoutTimeoutMilliSec;
    }

    public int getMaxStatements() {
        return maxStmtNum;
    }

    public void setMaxStatements(int maxStatements) {
        this.maxStmtNum = maxStatements;
    }

    public int getMaxPreStatements() {
        return maxPreStmtNum;
    }

    public void setMaxPreStatements(int maxPreStatements) {
        this.maxPreStmtNum = maxPreStatements;
    }

    public String getCheckStatement() {
        return checkStatement;
    }

    public void setCheckStatement(String checkStatement) {
        this.checkStatement = checkStatement;
    }

    public boolean isTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(boolean transactionMode) {
        this.transactionMode = transactionMode;
    }

    public int getJmxLevel() {
        return jmxLevel;
    }

    public void setJmxLevel(int jmxLevel) {
        this.jmxLevel = jmxLevel;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public boolean isOracle() {
        return isOracle;
    }

    public boolean isDB2() {
        return isDB2;
    }

    public boolean isUseOracleImplicitPSCache() {
        return useOracleImplicitPSCache;
    }

    public void setUseOracleImplicitPSCache(boolean useOracleImplicitPSCache) {
        this.useOracleImplicitPSCache = useOracleImplicitPSCache;
    }

    public RemoteDiagnosisCPConfig() {
//        this.poolName = poolName;
    }

    /**
     * Set the location of the jdbc properties file.
     */
    public void setPropertiesLocation(org.springframework.core.io.Resource configLocation) throws IOException {
        InputStream is = configLocation.getInputStream();
        Properties prop = new Properties();
        prop.load(is);
        this.setProperties(prop);
    }

    public void setProperties(Properties prop) {
        setConnUrl(prop.getProperty("jdbc.url"));
        setUsername(prop.getProperty("jdbc.username", null));
        setPassword(prop.getProperty("jdbc.password", null));
        driver = prop.getProperty("jdbc.driver", driver);
        verbose = Boolean.valueOf(prop.getProperty("jdbc.verbose", String.valueOf(verbose))).booleanValue();
        printSQL = Boolean.valueOf(prop.getProperty("jdbc.printSQL", String.valueOf(printSQL))).booleanValue();
        commitOnClose = Boolean.valueOf(prop.getProperty("jdbc.commit_on_close", String.valueOf(commitOnClose))).booleanValue();
        minConnNum = Integer.parseInt(prop.getProperty("jdbc.min_connections", String.valueOf(minConnNum)));
        maxConnNum = Integer.parseInt(prop.getProperty("jdbc.max_connections", String.valueOf(maxConnNum)));
        maxIdleMilliSec = Long.parseLong(prop.getProperty("jdbc.idle_timeout", String.valueOf(maxIdleMilliSec / 1000))) * 1000;
        checkOutTimeout = Long.parseLong(prop.getProperty("jdbc.checkout_timeout", String.valueOf(checkOutTimeout)));
        checkStatement = prop.getProperty("jdbc.check_statement", checkStatement);
        maxStmtNum = Integer.parseInt(prop.getProperty("jdbc.max_statements", String.valueOf(maxStmtNum)));
        maxPreStmtNum = Integer.parseInt(prop.getProperty("jdbc.max_prestatements", String.valueOf(maxPreStmtNum)));
        jmxLevel = Integer.parseInt(prop.getProperty("jdbc.jmx_level", String.valueOf(jmxLevel)));
        transactionMode = Boolean.parseBoolean(prop.getProperty("jdbc.transaction_mode", String.valueOf(transactionMode)));
        lazyInit = Boolean.parseBoolean(prop.getProperty("jdbc.lazy_init", String.valueOf(lazyInit)));
        infoSQLThreshold = Long.parseLong(prop.getProperty("jdbc.infoSQL", String.valueOf(infoSQLThreshold)));
        warnSQLThreshold = Long.parseLong(prop.getProperty("jdbc.warnSQL", String.valueOf(warnSQLThreshold)));
        useOracleImplicitPSCache = Boolean.valueOf(prop.getProperty("jdbc.use_implicit_ps_cache", String.valueOf(useOracleImplicitPSCache)));
        //setLoginTimeout(Integer.parseInt(prop.getProperty("jdbc.login_timeout", String.valueOf(0))));
        setQueryTimeout(Integer.parseInt(prop.getProperty("jdbc.query_timeout", String.valueOf(queryTimeout))));
        setConnectionInfo(prop.getProperty("jdbc.connection_info"));
        //set isOracle in setConnUrl method.
//        if (connUrl != null) {
//            isOracle = JdbcUtil.checkOracle(connUrl);
//        }
    }

    public static final String[] PROPERTIES = new String[]{
            "jdbc.driver", "jdbc.url", "jdbc.username", "jdbc.password", "jdbc.check_statement",
            "jdbc.verbose", "jdbc.printSQL", "jdbc.commit_on_close", "jdbc.transaction_mode", "jdbc.lazy_init",
            "jdbc.min_connections", "jdbc.max_connections", "jdbc.max_statements", "jdbc.max_prestatements",
            "jdbc.idle_timeout", "jdbc.checkout_timeout",
            "jdbc.jmx_level", "jdbc.infoSQL", "jdbc.warnSQL", "jdbc.use_implicit_ps_cache", "jdbc.connection_info",
            "jdbc.query_timeout"
    };
}
