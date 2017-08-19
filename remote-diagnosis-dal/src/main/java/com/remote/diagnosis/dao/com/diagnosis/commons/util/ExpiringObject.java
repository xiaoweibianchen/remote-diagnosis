package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import org.apache.commons.lang3.Validate;

import java.util.concurrent.TimeUnit;

/**
 * Created by wyshenjianlin on 14-5-15.
 */
public class ExpiringObject<V>{
    /**
     * 创建时间
     */
    private final long createTime = System.nanoTime();
    private final long expire;// 过期时间 <=0表示不过�?
    private final TimeUnit unit;  //超时单位
    private V value;

    public ExpiringObject(V value, long expire, TimeUnit unit) {
        Validate.notNull(value, "An expiring object cannot be null.");
        this.expire = expire;
        this.unit = unit;
        this.value = value;
    }

    public ExpiringObject(V value) {
        this(value, 0, TimeUnit.MILLISECONDS);
    }

    public long getCreateTime() {
        return createTime;
    }

    public boolean isExpired() {
        if (expire <= 0) {
            return false;
        }
        long expiredNan = System.nanoTime() - createTime;
        return expiredNan >= TimeUnit.NANOSECONDS.convert(expire, unit);
    }


    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpiringObject that = (ExpiringObject) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
