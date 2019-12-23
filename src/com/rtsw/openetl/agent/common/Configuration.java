package com.rtsw.openetl.agent.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author RT Software Studio
 */
public class Configuration {

    private Map<String, String> configuration = new HashMap<>();

    public Configuration() {
    }

    public Configuration(File file) throws Exception {
        loadFromProperties(file);
    }

    /**
     *
     * @param file
     * @throws Exception
     */
    public void loadFromProperties(File file) throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream(file));

        for (Object key : properties.keySet()) {
            String name = key.toString();
            String value = properties.getProperty(name);
            configuration.put(name, value);
        }

    }

    /**
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public String get(String name, String defaultValue) {
       Object o = configuration.get(name);
       if (o == null) {
           return (defaultValue);
       } else {
           return ((String) o);
       }
    }

    /**
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public int get(String name, int defaultValue) {
        Object o = configuration.get(name);
        if (o == null) {
            return (defaultValue);
        } else {
            try {
                return (Integer.parseInt((String) o));
            } catch (Exception e) {
                return (defaultValue);
            }
        }
    }

    /**
     *
     * @param name
     * @param defaultValue
     * @return
     */
    public boolean get(String name, boolean defaultValue) {
        Object o = configuration.get(name);
        if (o == null) {
            return (defaultValue);
        } else {
            try {
                return (Boolean.parseBoolean((String) o));
            } catch (Exception e) {
                return (defaultValue);
            }
        }
    }

    /**
     *
     * @param name
     * @param value
     */
    public void set(String name, String value) {
        configuration.put(name, value);
    }

    /**
     *
     * @param name
     * @param value
     */
    public void set(String name, int value) {
        configuration.put(name, new Integer(value).toString());
    }

    /**
     *
     * @param name
     * @param value
     */
    public void set(String name, boolean value) {
        configuration.put(name, new Boolean(value).toString());
    }

}
