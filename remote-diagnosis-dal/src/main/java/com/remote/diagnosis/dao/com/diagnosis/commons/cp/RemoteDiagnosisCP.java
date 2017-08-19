package com.remote.diagnosis.dao.com.diagnosis.commons.cp;


import com.remote.diagnosis.dao.com.diagnosis.commons.cp.util.JdbcUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.cp.util.OracleUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.Formatter;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.JMXUtil;
import com.remote.diagnosis.dao.com.diagnosis.commons.util.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class RemoteDiagnosisCP implements RemoteDiagnosisCPMBean {
    private static final Logger log = new Logger();
    
    private static final String[] classPaths = System.getProperty("java.class.path", "classes").split(System.getProperty("path.separator", ";"));

    private static final AtomicInteger POOL_ID = new AtomicInteger(0);

    /**
     * pool id
     */
    private final int poolId;
    
    /**
     * 连接池名�?
     * (跟配置文件名�?致，即jdbc.properties的名称是jdbc)
     */
    private String poolName;

    /**
     * 配置
     */
    private final RemoteDiagnosisCPConfig config;
    
    /**
     * 连接编号
     */
    private final AtomicInteger connectionNo = new AtomicInteger(0);
    
    /**
     * 池中可用连接�?
     */
    private final AtomicInteger validConnectionNum = new AtomicInteger(0);
    /**
     * 可用连接
     * 使用LinkedHashMap，并且用accessOrder�?
     * 确保�?近使用的连接在枚举器的最后，而最久使用的连接在最前面
     */
    private final Map<Integer, PooledConnection> validConnectionsPool = new ConcurrentHashMap<Integer, PooledConnection>();
    
    /**
     * 空闲连接Id�?
     * 数据结构：堆�?
     *      �?近使用的连接在堆栈的顶部，�?�最久未使用的连接在堆栈的底�?
     */
    private final LinkedStack<Integer> idleConnectionsId = new LinkedStack<Integer>();
    
    /**
     * 关闭标志
     */
    private AtomicBoolean shutdown = new AtomicBoolean(false);

    /**
     * 初始化标�?
     */
    private AtomicBoolean inited = new AtomicBoolean(false);

    /**
     * monitor thread
     */
    private Thread monitor;

    /**
     * true - config是从properties文件读入�?
     */
    private boolean configFromProperties = false;

    private BlockingQueue<NamedConnection> unclosedConnections = new LinkedBlockingQueue<NamedConnection>();

    RemoteDiagnosisCP(String poolName) throws SQLException {
        this.config = new RemoteDiagnosisCPConfig();
        this.config.setProperties(loadProperties(poolName));
        this.poolId = POOL_ID.getAndIncrement();
        this.poolName = poolName;
        this.configFromProperties = true;
        initPool();
    }
    
    public RemoteDiagnosisCP(RemoteDiagnosisCPConfig config) throws SQLException {
        this.config = config;
        this.poolId = POOL_ID.getAndIncrement();
        this.poolName = "UCP#" + this.poolId;
        this.configFromProperties = false;
        initPool();
    }

    // by wuhongqiang 2014.2.25
    // change the method name from startMonitor to initPool
    private void initPool() throws SQLException {
        if (this.config == null || this.config.getConnUrl() == null) {
            throw new SQLException("jdbc.url cannot be NULL");
        }
        if (config.getDriver() != null) {
            try {
                Class.forName(config.getDriver());
                log.info("load ", config.getDriver(), " ok");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e.toString(), e);
            }
        }
        boolean isOracle10 = config.isOracle() && DriverManager.getDriver(config.getConnUrl()).getMajorVersion() == 10;
        if (isOracle10 && config.isUseOracleImplicitPSCache()) {
            config.getConnectionProperties().setProperty(OracleUtil.ORACLE_FREECACHE_PROPERTY_NAME, OracleUtil.ORACLE_FREECACHE_PROPERTY_VALUE_TRUE);
        } else {
            config.getConnectionProperties().remove(OracleUtil.ORACLE_FREECACHE_PROPERTY_NAME);
        }
        //设置native驱动的超时设�?
        //add by wuhongqiang. 2014.07.04
        if (config.getCheckoutTimeoutMilliSec() > 0) {
            if (config.isOracle()) {
                config.getConnectionProperties().setProperty(OracleUtil.CONNECT_TIMEOUT, String.valueOf(config.getCheckoutTimeoutMilliSec()));
            } else if (config.isMySQL()) {
                config.getConnectionProperties().setProperty(MySQLPooledConnection.CONNECT_TIMEOUT, String.valueOf(config.getCheckoutTimeoutMilliSec()));
            }

        }
        if (config.getQueryTimeout() > 0) {
            if (config.isOracle()) {
                config.getConnectionProperties().setProperty(OracleUtil.SOCKET_TIMEOUT, String.valueOf(config.getQueryTimeout()*1000));
                config.getConnectionProperties().setProperty(OracleUtil.SOCKET_TIMEOUT_LOW_VER, String.valueOf(config.getQueryTimeout()*1000));
            } else if (config.isMySQL()) {
                config.getConnectionProperties().setProperty(MySQLPooledConnection.SOCKET_TIMEOUT, String.valueOf(config.getQueryTimeout()*1000));
            }
        }
        if (inited.getAndSet(true)) {
            return;
        }
        if (! config.isLazyInit()) {
            //尝试建立�?条连�?
            if (validConnectionNum.get() < config.getMinConnections()) {
                newConnection(false);
            }
        }

        monitor = new CPMonitor();
        monitor.setName("CPM:" + poolName);
        monitor.setDaemon(true);
        monitor.start();
        
        if (config.getJmxLevel() > 0) {
            JMXUtil.register(this.getClass().getPackage().getName() + ":type=pool-" + poolName, this);
            JMXUtil.register(this.getClass().getPackage().getName() + ":type=pool-" + poolName + ",name=config", config);
        }
    }

    public boolean isShutdown() {
        return shutdown.get();
    }

    /**
     * 关闭连接�?
     */
    public void shutdown() {
        if (shutdown.getAndSet(true)) {
            return;
        }
        ConnectionFactory.remove(poolName);
        if (monitor != null) {
            monitor.interrupt();
            try {
                monitor.join();
            } catch (InterruptedException e) {
            }
        }
        for (int i = 0; i < 10; i++) { //�?多检�?10次，超过之后强制关闭
            Integer[] connIds = idleConnectionsId.toArray();
            for (Integer connId: connIds) {
                PooledConnection pc = validConnectionsPool.get(connId);
                try {
                    pc.lock(); //锁住连接，不允许checkout
                } catch (InterruptedException e) {
                    continue;
                }
                try {
                    if (pc.isCheckOut()) {
                        //已经checkout，则停止�?�?
                        break;
                    }
                    if (! closeConnection(pc)) {
                        //连接栈底部不是当前连�?
                        //说明连接正等待被�?�?
                        break;
                    }
                } finally {
                    pc.unlock();
                }
            }
            if (validConnectionNum.get() <= 0) {
                break;
            }
            logVerboseInfo(true);
            idleConnectionsId.awaitNotEmpty(1, TimeUnit.SECONDS);
        }
        for (Map.Entry<Integer, PooledConnection> e: validConnectionsPool.entrySet()) {
            PooledConnection pc = e.getValue();
            if (pc.isCheckOut()) {
                log.info("force closing ... ", pc.getConnectionName(), " checkout by ", pc.getThreadCheckOut().getName(),
                        " for " + pc.getCheckOutTime() + " ms[", (pc.isBusying() ? "BUSYING" : "IDLE"), "]");
            }
            pc.close();
            validConnectionNum.decrementAndGet();
        }
        validConnectionsPool.clear();
        JMXUtil.unregister(this.getClass().getPackage().getName() + ":type=pool-" + poolName);
        JMXUtil.unregister(this.getClass().getPackage().getName() + ":type=pool-" + poolName + ",name=config");
    }
    
    public void reloadProperties() {
        if (configFromProperties) {
            Properties prop = loadProperties(poolName);
            config.setProperties(prop);
        }
    }
    
    /**
     * 读取配置文件
     */
    private Properties loadProperties(String propfile) {
        Properties prop = new Properties();
        File pfile = null;
        for (int i = 0; i <= classPaths.length; i++) {
            if (i == classPaths.length) {
                pfile = new File("./" + propfile + ".properties");
            } else {
                pfile = new File(classPaths[i] + "/" + propfile + ".properties");
            }
            if (pfile.exists()) {
                break;
            }
        }
        if (pfile != null && pfile.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(pfile);
                prop.load(fis);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
                log.warn(e);
            } finally {
                // modify by shenjl 修改资源泄露问题
                JdbcUtil.closeQuietly(fis);
            }
        } else {
            ResourceBundle rb = ResourceBundle.getBundle(propfile);
            for (String property : RemoteDiagnosisCPConfig.PROPERTIES) {
                if (rb.containsKey(property)) {
                    prop.setProperty(property, rb.getString(property));
                }
            }
        }
        return prop;
    }
    
    /**
     * 从池中poll连接
     * @return
     * @throws java.sql.SQLException
     */
    public Connection getConnection() throws SQLException {
        return getConnection(! config.isTransactionMode());
    }

    /**
     * 从池中poll连接
     * @param autoCommit
     * @return
     * @throws java.sql.SQLException
     */
    public Connection getConnection(boolean autoCommit) throws SQLException {
        if (shutdown.get()) {
            throw new SQLException("connection pool is shutdown", "08001");
        }
        long start = System.nanoTime();
        Integer connId = idleConnectionsId.pop();
        if (connId == null && ! config.isLazyInit()) {
            connId = newConnection(true);
        }
        if (connId == null) {
            if (validConnectionNum.get() >= config.getMaxConnections()) {
                log.info("connections of ", poolName, " to ", config.getConnUrl(), " exhausted, wait ", config.getCheckoutTimeoutMilliSec(), " ms for idle connection");
            }
            try {
                connId = idleConnectionsId.pop(config.getCheckoutTimeoutMilliSec(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.info(e);
            }
        }
        if (connId == null) {
            //add by wuhq. 2014.02.19 在连接池耗尽时，打印连接池的状�??
            logVerboseInfo(true);
            throw new SQLException("Timed out waiting for a free available connection of " + poolName + " to " + config.getConnUrl(), "08001");
        }
        PooledConnection pconn = validConnectionsPool.get(connId);
        try {
            Connection conn = pconn.checkOut(autoCommit);
            if (config.isVerbose()) {
                log.debug(pconn.getConnectionName(), ".getConnection(", autoCommit, "), use ", Formatter.formatNS(System.nanoTime() - start), " ns");
            }
            return conn;
        } catch (SQLException e) {
            checkIn(pconn);
            throw e;
        }
    }
    
    void checkIn(PooledConnection pconn) {
        int connId = pconn.getConnectionId();
        idleConnectionsId.push(connId);
    }

    /**
     * Return poolName.
     * @return poolName
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * new one pooled connection
     * @param directReturn
     * @return connId
     * @throws SQLException
     */
    private Integer newConnection(boolean directReturn) throws SQLException {
        if (validConnectionNum.incrementAndGet() <= config.getMaxConnections()) {
            //当前连接�? < �?大连接数，则创建连接
            Integer connId = connectionNo.getAndIncrement();
            try {
                PooledConnection pconn;
                if (config.isOracle()) {
                    pconn = new OraclePooledConnection(RemoteDiagnosisCP.this, connId);
                } else if (config.isMySQL()) {
                    pconn = new MySQLPooledConnection(RemoteDiagnosisCP.this, connId);
                } else if (config.isDB2()) {
                    pconn = new DB2PooledConnection(RemoteDiagnosisCP.this, connId);
                } else {
                    pconn = new PooledConnection(RemoteDiagnosisCP.this, connId);
                }
                validConnectionsPool.put(connId, pconn);
                if (directReturn) {
                    return connId;
                } else {
                    idleConnectionsId.push(connId);
                }
                if (config.isVerbose()) {
                    log.info(poolName, " +)", validConnectionNum.get(), " connections to ", config.getConnUrl());
                }
            } catch (SQLException e) {
                validConnectionNum.decrementAndGet();
                throw e;
            }
        } else {
            validConnectionNum.decrementAndGet();
        }
        return null;
    }

    /**
     * close one pooled connection
     * @param pc
     * @return
     */
    private boolean closeConnection(PooledConnection pc) {
        //移除连接栈底部连�?
        if (! idleConnectionsId.removeStackBottom(pc.getConnectionId())) {
            //连接栈底部不是当前连�?
            //说明连接正等待被�?�?
            //则停止检�?
            return false;
        }
        validConnectionNum.decrementAndGet();
        validConnectionsPool.remove(pc.getConnectionId());
        pc.close();
        if (config.isVerbose()) {
            log.info(poolName, " -)", validConnectionNum.get() ," connections to ", config.getConnUrl());
        }
        return true;
    }

    /**
     * log连接的状态信�?
     */
    private void logVerboseInfo(boolean verbose) {
        //显示当前活动连接的状�?
        int i = 0;
        for (Map.Entry<Integer, PooledConnection> e: validConnectionsPool.entrySet()) {
            PooledConnection pc = e.getValue();
            if (pc.isCheckOut()) {
                i++;
                long usedMS = pc.getCheckOutTime();
                if (usedMS <= pc.getInfoSQLThreshold() && log.isDebugEnabled()) {
                    log.debug(pc.getConnectionName(), " checkout by ", pc.getThreadCheckOut().getName(), " for " + usedMS + " ms[", (pc.isBusying() ? "BUSYING" : "IDLE"), "]");
                } else if (usedMS <= pc.getWarnSQLThreshold() && log.isInfoEnabled()) {
                    log.info(pc.getConnectionName(), " checkout by ", pc.getThreadCheckOut().getName(), " for " + usedMS + " ms[", (pc.isBusying() ? "BUSYING" : "IDLE"), "]");
                } else if (log.isWarnEnabled()) {
                    log.warn(pc.getConnectionName(), " checkout by ", pc.getThreadCheckOut().getName(), " for " + usedMS + " ms[", (pc.isBusying() ? "BUSYING" : "IDLE"), "]");
                }
            }
        }
        if (verbose) {
            log.info(poolName, ": checkout:", i, "/connected:", validConnectionNum.get(), "/max:", config.getMaxConnections());
        } else {
            log.debug(poolName, ": checkout:", i, "/connected:", validConnectionNum.get(), "/max:", config.getMaxConnections());
        }
    }

    void offerUnclosedConnection(Connection connection, String connectionName) {
        unclosedConnections.offer(new NamedConnection(connection, connectionName));
    }

    void closeUnclosedConnection() {
        final int c = unclosedConnections.size();
        for (int i = 0; i < c; i++) {
            NamedConnection namedConnection = unclosedConnections.poll();
            if (namedConnection == null) {
                break;
            }
            try {
                namedConnection.retryCloseCount++;
                try {
                    namedConnection.connection.close();
                } catch (SQLException e) {
                    try {
                        namedConnection.connection.rollback();
                    } catch (SQLException ignr) {}
                    namedConnection.connection.close();
                }
                log.info(namedConnection.connectionName, " finally be closed!");
            } catch (SQLException e) {
                log.error("closeUnclosedConnection(", namedConnection.connectionName, ")[", namedConnection.retryCloseCount, "] error: ", e.toString());
                if (namedConnection.retryCloseCount < 10) {
                    unclosedConnections.offer(namedConnection);
                }
            }
        }
    }

    private class NamedConnection {
        final Connection connection;
        final String connectionName;
        int retryCloseCount = 0;

        private NamedConnection(Connection connection, String connectionName) {
            this.connection = connection;
            this.connectionName = connectionName;
        }
    }

    private class CPMonitor extends Thread {
        private ExecutorService executorService = Executors.newSingleThreadExecutor();

        /**
         * （从堆栈底部的连接开始检查）
         * 空闲连接的检查：
         *  1、关闭超过minConnections设置的空闲连�?
         *  2、对空闲的连接进行存活检�?
         * @return 下次�?查的时间间隔(ms)（根据保留的堆栈底部的连接的�?后check时间计算得出�?
         * @throws InterruptedException
         */
        private long idleConnectionCheckOrClose() throws InterruptedException {
            long timeToNextCheck = config.getIdleTimeoutMilliSec();
            Integer[] connIds = idleConnectionsId.toArray();
            for (Integer connId: connIds) {
                PooledConnection pc = validConnectionsPool.get(connId);
                pc.lock(); //锁住连接，不允许checkout
                try {
                    if (pc.isCheckOut()) {
                        //已经checkout，则停止�?�?
                        break;
                    }
                    //下次�?测时�?=�?入时�?+�?测间�?
                    timeToNextCheck = pc.getTimeCheckIn() + config.getIdleTimeoutMilliSec() - System.currentTimeMillis();
                    if (timeToNextCheck <= 0) {
                        //达到�?要检测或回收的时�?
                        //置下次检测时�?=当前时间+�?大检测间�?
                        timeToNextCheck = config.getIdleTimeoutMilliSec();
                        if (validConnectionNum.get() > config.getMinConnections()) {
                            //当前连接�?>�?少连接数，则回收该连�?
                            if (! closeConnection(pc)) {
                                //连接栈底部不是当前连�?
                                //说明连接正等待被�?�?
                                //则停止检�?
                                break;
                            }
                        } else {
                            //否则，检测该连接
                            //pc.doCheck();
                            asyncCheckConnection(pc);
                        }
                    } else {
                        //FIXME do break is correct?
                        break;
                    }
                } finally {
                    pc.unlock();
                }
            }
            return timeToNextCheck;
        }

        private void asyncCheckConnection(final PooledConnection pooledConnection) {
            Future<?> future = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    pooledConnection.doCheck();
                }
            });
            try {
                future.get(config.getIdleTimeoutMilliSec(), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("get connection: ", pooledConnection.getConnectionName(), " check result error: ", e);
                pooledConnection.close();
            }
        }

        /**
         * 保持�?小连接数
         */
        private void newMoreConnections(long waitTime) throws InterruptedException {
            try {
                while (validConnectionNum.get() < config.getMinConnections()) {
                    newConnection(false);
                }
            } catch (SQLException e) {
                log.warn(e);
            }
            long nanos = TimeUnit.MILLISECONDS.toNanos(waitTime);
            final long timeout = System.nanoTime() + nanos;
            while (timeout-System.nanoTime() > 0) {
                nanos = idleConnectionsId.awaitRequireMore(timeout-System.nanoTime(), TimeUnit.NANOSECONDS);
                if (nanos > 0) {
                    try {
                        newConnection(false);
                    } catch (SQLException e) {
                        log.warn(e);
                    }
                } else {
                    break;
                }
            }
        }

        public void run() {
            log.info(getName(), " start!");
            long idleTimeout = config.getIdleTimeoutMilliSec();
            while (! shutdown.get()) {
                try {
                    //保持�?小连接数
                    newMoreConnections(idleTimeout);
                    //尝试关闭游离池外的raw connection
                    closeUnclosedConnection();
                    //�?查连接可用�?�，并关闭额外的连接
                    idleTimeout = idleConnectionCheckOrClose();
                    //log连接池的信息
                    logVerboseInfo(config.isVerbose());
                } catch (InterruptedException e) {
                    if (shutdown.get()) {
                        break;
                    }
                } catch (Exception e) {
                    idleTimeout = config.getIdleTimeoutMilliSec();
                    log.warn(e);
                } catch (Throwable t) {
                    idleTimeout = config.getIdleTimeoutMilliSec();
                    log.error(t);
                }
            }
            executorService.shutdown();
            try {
                if (! executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException ignr) {}
            log.info(getName(), " quit!");
        }
    }

    private static class LinkedStack<E> {
        private LinkedList<E> stack;
        private final ReentrantLock operLock = new ReentrantLock();
        private final Condition notEmpty = operLock.newCondition();
        private final Condition requireMore = operLock.newCondition();

        public LinkedStack() {
            stack = new LinkedList<E>();
        }
        
        public int size() {
            return stack.size();
        }
        
        public boolean removeStackBottom(E e) {
            operLock.lock();
            try {
                if (stack.size() == 0) {
                    return false;
                }
                E x = stack.getFirst();
                if (x.equals(e)) {
                    stack.removeFirst();
                    return true;
                } else {
                    return false;
                }
            } finally {
                operLock.unlock();
            }
        }
        
        public void push(E e) {
            int c = -1;
            operLock.lock();
            try {
                c = stack.size();
                stack.addLast(e);
                if (c == 0) {
                    notEmpty.signal();
                }
            } finally {
                operLock.unlock();
            }
        }
        
        public E pop() {
            operLock.lock();
            try {
                if (0 == stack.size()) {
                    return null;
                }
                return stack.removeLast();
            } finally {
                operLock.unlock();
            }
        }

        /**
         * 等待请求更多(连接)
         * @param timeout �?长的等待时间
         * @param unit
         * @return 剩余的等待时�?(ns)
         * @throws InterruptedException
         */
        public long awaitRequireMore(long timeout, TimeUnit unit) throws InterruptedException {
            operLock.lockInterruptibly();
            try {
                try {
//                    log.info("wait ", unit.toMillis(timeout), " ms...");
                    return requireMore.awaitNanos(unit.toNanos(timeout));
                } catch (InterruptedException e) {
                    requireMore.signal();
                    throw e;
                }
            } finally {
                operLock.unlock();
            }
        }

        /**
         * 等待直到非空
         * @param timeout
         * @param unit
         * @return
         */
        public boolean awaitNotEmpty(long timeout, TimeUnit unit) {
            try {
                operLock.lockInterruptibly();
            } catch (InterruptedException e) {
                return true;
            }
            try {
                return notEmpty.await(timeout, unit);
            } catch (InterruptedException e) {
                return true;
            } finally {
                operLock.unlock();
            }
        }

        public E pop(long timeout, TimeUnit unit) throws InterruptedException, SQLException {
            long nanos = unit.toNanos(timeout);
            operLock.lockInterruptibly();
            try {
                while (true) {
                    if (stack.size() > 0) {
                        E x = stack.removeLast();
                        if (stack.size() > 0) {
                            notEmpty.signal();
                        }
                        return x;
                    } else {
                        //stack is empty, signal for more connection
                        requireMore.signal();
                    }
                    if (nanos <= 0) {
                        return null;
                    }
                    try {
                        nanos = notEmpty.awaitNanos(nanos);
                    } catch (InterruptedException ie) {
                        notEmpty.signal(); // propagate to a non-interrupted thread
                        throw ie;
                    }
                }
            } finally {
                operLock.unlock();
            }
        }
        
        public Integer[] toArray() {
            operLock.lock();
            try {
                Integer[] ret = new Integer[stack.size()];
                return stack.toArray(ret);
            } finally {
                operLock.unlock();
            }
        }
        
    }
    
    public int getActiveConnectionsCount() {
        return validConnectionNum.get();
    }

    public int getIdleConnectionsCount() {
        return idleConnectionsId.size();
    }

    public RemoteDiagnosisCPConfig getConfig() {
        return config;
    }

    public int getPoolId() {
        return poolId;
    }

    public void setPoolName(String poolName) {
        if (! configFromProperties) {
            this.poolName = poolName;
            if (this.monitor != null) {
                this.monitor.setName("CPM:" + poolName);
            }
        }
    }

}
