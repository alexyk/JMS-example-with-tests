package com.berc.jpm.utils;

import javax.ejb.Singleton;
import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by rasto on 14.4.2015.
 * properties configuration reader
 */
@Stateless
@Singleton
public class ConfigReader {

    /**
     * read property name. If not found defaultValue returned
     * @param name
     * @param defaultValue
     * @return found property value, otherwise defaultValue
     */
    public String readProperty(String name, String defaultValue) {
        String configName = "../config.properties";
        Properties properties = new Properties();
        try {
            InputStream input = getClass().getResourceAsStream(configName);
            if (input == null) {
                System.out.println("Sorry, unable to find " + configName);
                return defaultValue;
            }
            properties.load(input);
            input.close();

            return properties.getProperty(name);
        } catch (IOException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
