package com.rtsw.openetl.agent.utils;

public class ExceptionUtils {

    public static void handleException(Exception exception) {

        System.err.println(exception.getMessage());
        exception.printStackTrace(System.err);

    }

}
