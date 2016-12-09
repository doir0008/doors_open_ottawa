package com.algonquincollege.doir0008.doorsopenottawa;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Manage HTTP connections.
 *
 * Supported methods:
 * + getData() :: String
 *
 * @author David Gassner
 */

public class HttpManager {

    /**
     * Return the HTTP response from uri
     * @param uri Uniform Resource Identifier
     * @return String the response; null when exception
     */
    public static String getData(String uri) {

        BufferedReader reader = null;

        try {
            // open the URI
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            // make a buffered reader
            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // read the HTTP response from URI one-line-at-a-time
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            // return the HTTP response
            return sb.toString();
            // exception handling: a) print stack-trace, b) return null
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }




    public static String getData(String uri, String userName, String password) {

        BufferedReader reader = null;
        HttpURLConnection con = null;

        byte[] loginBytes = (userName + ":" + password).getBytes();
        StringBuilder loginBuilder = new StringBuilder()
                .append("Basic ")
                .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));

        try {
            URL url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();

            con.addRequestProperty("Authorization", loginBuilder.toString());

            StringBuilder sb = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                int status = con.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }


}