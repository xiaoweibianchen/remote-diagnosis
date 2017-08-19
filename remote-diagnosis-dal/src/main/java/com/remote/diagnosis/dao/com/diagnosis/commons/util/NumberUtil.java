package com.remote.diagnosis.dao.com.diagnosis.commons.util;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created with IntelliJ IDEA.
 * User: wyshenjianlin
 * Date: 13-11-5
 * Time: 上午10:02
 * To change this template use File | Settings | File Templates.
 */
public class NumberUtil extends NumberUtils {
    public static double roundTo(double val, int places) {
        double factor = Math.pow(10, places);
        return ((int) ((val * factor) + 0.5)) / factor;
    }
}
