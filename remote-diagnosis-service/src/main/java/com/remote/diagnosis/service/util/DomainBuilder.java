package com.remote.diagnosis.service.util;

/**
 * Created by heliqing on 2017/3/22.
 */
public interface DomainBuilder<D,S>{

    D builde(S source);
}

