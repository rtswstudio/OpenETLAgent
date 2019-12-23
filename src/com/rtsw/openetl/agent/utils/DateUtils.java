package com.rtsw.openetl.agent.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");

    public static long millisecondsBetween(Date start, Date end) {
        if (start == null || end == null) {
            return (-1);
        }
        return (Math.abs(end.getTime() - start.getTime()));
    }

    public static long secondsBetween(Date start, Date end) {
        if (start == null || end == null) {
            return (-1);
        }
        return (Math.abs((end.getTime() - start.getTime()) / 1000));
    }

    public static String format(Date date) {
        return (SDF.format(date));
    }

}
