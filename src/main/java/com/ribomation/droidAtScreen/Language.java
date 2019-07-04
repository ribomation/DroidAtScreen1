/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ribomation.droidAtScreen;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Jhuan
 */
public class Language extends Properties {

    private static final long serialVersionUID = 1L;

    public Language(String lenguageCode) {
        getProperties(lenguageCode);
    }

    private void getProperties(String lenguageCode) {

        try {
            String path = "/languages/" + lenguageCode + ".properties";
            InputStream stream = getClass().getResourceAsStream(path);
            if (stream != null) {
                this.load(stream);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Override
    public String getProperty(String key) {
        String value = super.getProperty(key);
        return (value != null) ? value : "unknown";
    }
}
